/**
 * Copyright (C) 2009, Gajo Csaba
 *
 * This file is free software; the author gives unlimited 
 * permission to copy and/or distribute it, with or without
 * modifications, as long as this notice is preserved.
 * 
 * For more information read the LICENSE file that came
 * with this distribution. 
 */
package com.infineon.cv.makefile.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Makefile parser
 * 
 * @author cgajo
 */
public class MakefileParser implements Cloneable {

	private static final int STATE_NONE = 0;
	private static final int STATE_COMMENT = 1;
	private static final int STATE_COMMENT_STACKED = 2;
	private static final int STATE_ALPHANUM = 3;
	private static final int STATE_VARIABLE = 4;
	private static final int STATE_VARIABLE_APPEND = 5;
	private static final int STATE_VARIABLE_DEFINE = 6;
	private static final int STATE_TARGET = 7;
	private static final int STATE_AFTER_TARGET = 8;
	private static final int STATE_COMMAND = 9;
	private List<Target> targets = new ArrayList<Target>();
	private final VariableManager varManager;
	private Counters keywordCounts = new MakefileParser.Counters();

	/** Create a new parser */
	public MakefileParser(VariableManager varManager) {
		this.varManager = varManager;
	}

	/** Parse the Makefile */
	public void parse(final File file) throws IOException, ParseException {
		parse(new FileReader(file));
	}

	/** Parse the Makefile from a stream */
	public void parse(final InputStream in) throws IOException, ParseException {
		parse(new InputStreamReader(in));
	}

	/** Parse the Makefile from the reader */
	public void parse(final Reader reader) throws IOException, ParseException {
		BufferedReader in = new BufferedReader(reader);
		int state = STATE_NONE;
		char ch = ' ';
		int index = 0, start = 0, n = -1, wordCount = 0;
		boolean finished = false, add = false, override = false, skipRead = false, leadSpace = true;
		boolean cannotBeCommand = false, orderOnly = false, uniqueName = true, expandNow = false;
		boolean define = false, include = false, ifdef = false, ifdefCond = false, ifdefInProcess = false, ifdefCondValue = false;
		StringBuffer strbuf = new StringBuffer();
		Stack<String> stack = new Stack<String>();
		Stack<String> targetNames = new Stack<String>();
		Stack<Integer> stateStack = new Stack<Integer>();
		LinkedList<String> normalPrereqs = new LinkedList<String>(), orderOnlyPrereqs = new LinkedList<String>();

		while (!finished) {
//			 System.out.println("ch = '" + ch + "' (byte = " + (int)ch +
//			 "), wordcount = " + wordCount + ", state = " + state(state) +
//			 ", strbuf = " + strbuf.toString());
			if (!skipRead) {
				n = in.read();
			}
			index++;
			if (n == -1 && state != STATE_AFTER_TARGET && state != STATE_VARIABLE && state != STATE_VARIABLE_APPEND && state != STATE_COMMAND) {
				finished = true;
				skipRead = false;
			} else {
				if (skipRead) {
					skipRead = false;
				} else {
					ch = (char) n;
				}
				switch (state) {
				case STATE_NONE:
					if (ch == '\n' || ch == '\r' || ch == '\0' || ch == '\\' || ch == ' ') {
						cannotBeCommand = true;
						continue;
					} else if (ch == '#') {
						state = STATE_COMMENT;
						wordCount = 0;
						define = false;
						include = false;
						cannotBeCommand = true;
					} else if (!cannotBeCommand && ch == '\t') {
						state = STATE_COMMAND;
						wordCount = 0;
						define = false;
						include = false;
						strbuf = new StringBuffer(ch);
					} else {
						start = index;
						state = STATE_ALPHANUM;
						wordCount = 0;
						define = false;
						include = false;
						cannotBeCommand = true;
						strbuf = new StringBuffer(100);
						strbuf.append(ch);
					}
					break;
				case STATE_COMMENT:
				case STATE_COMMENT_STACKED:
					if (ch == '\\') { // lines ending with \ continue the
										// comment
						in.read();
						continue;
					} else if (ch == '\n' || ch == '\r' || ch == '\0') {
						if (state == STATE_COMMENT_STACKED && !stateStack.isEmpty())
							state = stateStack.pop();
						else
							state = STATE_NONE;
					}
					break;
				case STATE_ALPHANUM:
					add = true;
					if (ch == ' ') { // only multiple target names may have
										// spaces between
						add = false;
						wordCount++;
						if (strbuf.length() > 0) {
							String word = strbuf.toString();
							stack.push(word);
							if (wordCount == 1) {
								if (word.equals("override")) { // this is an
																// override
																// variable
									override = true;
								} else if (word.equals("define")) { // define
																	// variable
									define = true;
								} else if (word.equals("include") || word.equals("-include")) { // include
																								// another
																								// Makefile
									include = true;
								} else if (word.equals("ifdef")) {
									ifdef = true;
									ifdefCondValue = true;
									keywordCounts.ifdef_count++;
								} else if (word.equals("ifndef")) {
									ifdef = true;
									ifdefCondValue = false;
									keywordCounts.ifdef_count++;
								}
							}
							strbuf = new StringBuffer();
						}
					} else if (ch == '=') {
						// a variable
						state = STATE_VARIABLE;
						start = index;
						add = false;
						leadSpace = true;
						if (strbuf.length() > 0) {
							stack.push(strbuf.toString());
							strbuf = new StringBuffer();
						}
					} else if (ch == '?') {
						add = false;
						// a variable expanded only if undef so far
						ch = (char) in.read();
						if (ch == '=') {
							// yep, it's definitively it
							String varID = null;
							if (strbuf.length() == 0)
								varID = stack.pop();
							else {
								varID = strbuf.toString();
								strbuf = new StringBuffer();
							}
							if (varManager.isVarDefined(varID)) {
								// we ignore this
								state = STATE_COMMENT;
							} else {
								state = STATE_VARIABLE;
								leadSpace = true;
								start = index;
								stack.push(varID); // var name is on stack
							}

						} else {
							error(index, ch, "=");
						}
					} else if (ch == '+') { // append to variable
						add = false;
						ch = (char) in.read();
						if (ch == '=') {
							// yep, it's definitively it
							String varID = null;
							if (strbuf.length() == 0)
								varID = stack.pop();
							else {
								varID = strbuf.toString();
								strbuf = new StringBuffer();
							}
							if (varManager.isVarDefined(varID)) {
								// already exists, so we append to it
								state = STATE_VARIABLE_APPEND;
							} else {
								// not exists yet, so we create it like ordinary
								// variable
								state = STATE_VARIABLE;
								start = index;
							}
							leadSpace = true;
							stack.push(varID);
						} else {
							error(index, ch, "=");
						}
					} else if (ch == ':') { // this can be a variable or a
											// target
						add = false;
						ch = (char) in.read();
						if (ch == '=') {
							// it's a variable with immediate expansion
							String varID = null;
							if (strbuf.length() == 0)
								varID = stack.pop();
							else {
								varID = strbuf.toString();
								strbuf = new StringBuffer();
							}
							if (varManager.isVarDefined(varID) && !override) {
								// already exists, so we ignore the rest of the
								// line
								state = STATE_COMMENT;
							} else {
								// not exists yet or is override mode, so we
								// create it like ordinary variable
								state = STATE_VARIABLE;
								expandNow = true;
								leadSpace = true;
								start = index;
								stack.push(varID); // var name is on stack
							}
						} else {
							// it's not a variable (=> it's a target)
							if (ch == ':') { // :: = not unique name, always add
												// to the list
								uniqueName = false;
							} else {
								uniqueName = true;
								skipRead = true; // it's nothing we can process
													// in this state, let's
													// continue in another
							}
							state = STATE_TARGET;
							leadSpace = true;
							add = true;
							orderOnly = false;
							if (strbuf.length() > 0) {
								stack.push(strbuf.toString());
								strbuf = new StringBuffer();
							}
							start = index;
							if (stack.isEmpty()) { // let's just check if
													// everything's ok
								error(index, ch, "target names");
							} else {
								// let's empty the stack
								while (!stack.isEmpty()) {
									targetNames.push(stack.pop());
								}
								// now we can use the stack for collecting the
								// prerequisites
							}
						}

					} else if (ch == '#') {
						add = false;
						if (strbuf.length() > 0) {
							stack.push(strbuf.toString());
							strbuf = new StringBuffer();
						}
						stateStack.add(state);
						state = STATE_COMMENT_STACKED;
					} else if ((ch == '\n' || ch == '\r' || ch == '\0') && (define || include || ifdef || ifdefInProcess)) {
						if (define) {
							if (stack.size() == 1) {
								stack.pop(); // pop out the "define" keyword
							} else if (stack.size() == 2) {
								String tmp = stack.pop(); // var name
								stack.pop(); // define keyword
								stack.push(tmp);
							}
							state = STATE_VARIABLE_DEFINE;
							start = index;
							add = false;
							leadSpace = true;
							if (strbuf.length() > 0) {
								stack.push(strbuf.toString());
								strbuf = new StringBuffer();
							}
						}

						else if (ifdef) {
							// At this point, the stack would have ifdef keyword
							// and the variable if there are spaces after the
							// variable name.
							// if there are no spaces, the string buffer has the
							// variable.
							add = false; // Do not add to the string buffer.
							String varToBeChecked = null;
							if ((strbuf.length() > 0) && (stack.size() == 2)) {
								throw new IllegalStateException("Wrong syntax of ifdef");
							}
							if (strbuf.length() > 0) {
								varToBeChecked = strbuf.toString();

							} else if (stack.size() == 2) {
								varToBeChecked = stack.pop();

							}
							stack.pop(); // Pop the ifdef word
							strbuf = new StringBuffer(); // Clear the buffer
							if ("".equals(varManager.getUnexpandedValue(varToBeChecked)) == ifdefCondValue) {
								// ifdef condition is false. Hence read and
								// discard until you reach the else part
								ifdefCond = false;
								if (handleIfdefCondFalse(in) == true) {
									// Endif found first
									ifdefInProcess = false;
									
								} else {
									// Else found first
									ifdefCond = true;
									ifdefInProcess = true;
								}
								state = STATE_NONE;

							} else {
								// ifdef condition is true
								ifdefCond = true;
								state = STATE_NONE;
								ifdefInProcess = true;
							}
							ifdef = false;// resetting
						} // End of handling of ifdef

						else if (ifdefInProcess) {
							add = false;
							boolean ifdefDone = false;
							String currWord = null;
							if (strbuf.length() > 0) {
								currWord = strbuf.toString();
							} else {
								// if the keyword has a trailing space, it would
								// have been in the stack.
								if (stack.size() == 1) {
									currWord = stack.pop();
								}
							}

							// the ifdef block has not been closed yet.
							if (ifdefCond) {
								// if the ifdef condition is true, the true
								// condition block would have been evaluated
								// Checking if else keyword is read

								if (currWord.equals("else")) {
									// Read and discard until endif.
									discardRestOfIfdefBlock(in);
									ifdefDone = true;
								}
								if (currWord.equals("endif")) {
									ifdefDone = true;
								}
							} else {
								// ifdefcondition is false , else block should
								// have been evaluated by now.

								if (currWord.equals("endif")) {
									keywordCounts.ifdef_count--;
									if (keywordCounts.ifdef_count != 0) {

										// There are other blocks to be read and
										// discarded, which by itself can have
										// many ifdef-endif blocks.
										discardRestOfIfdefBlock(in);
										ifdefDone = true;
									}
								}
							}

							if (ifdefDone) {
								state = STATE_NONE;
								strbuf = new StringBuffer();
								ifdefInProcess = false;
								ifdefDone = false;
								keywordCounts.resetCounters();
							}
						}// End of ifdefInProcess block
						else if (include) { // include Makefile
							try {
								String fileName = stack.pop();
								if (strbuf.length() > 0) {
									fileName = strbuf.toString();
									strbuf = new StringBuffer();
								} else {
									stack.pop(); // pop out the "include"
													// keyword
								}
								state = STATE_NONE;
								add = false;

								Variable fileVar = new Variable("fileName_tmp_" + Math.random(), fileName);
								fileVar.expand(varManager);
								fileName = fileVar.getValue().trim();

								File includefile = null;
								if (fileName.charAt(0) == '/') // absolute
									includefile = new File(fileName);
								else
									// relative
									includefile = new File(System.getProperty("user.dir"), fileName);
								if (!includefile.exists()) {
									System.err.println("The include file " + includefile.getAbsolutePath() + " doesn't exist.");
									System.err.println(System.getProperty("user.dir"));
								} else if (includefile.isDirectory()) {
									System.err.println("The include file " + includefile.getAbsolutePath() + " is a directory.");
								} else {
									// the clone will use the same varManager
									// and targetList
									MakefileParser tmpParser = (MakefileParser) clone();
									tmpParser.parse(includefile);
								}
							} catch (CloneNotSupportedException cnsex) {
								throw new ParseException(cnsex.toString(), index);
							}
						}
					}
					if (add)
						strbuf.append(ch);
					break;

				case STATE_VARIABLE:
				case STATE_VARIABLE_APPEND:
				case STATE_VARIABLE_DEFINE:
					// in this moment, we have the var name on stack, and have
					// crossed beyond the =
					add = true;
					if (ch == ' ' || ch == '\t') {
						if (leadSpace) {
							if (strbuf.length() > 0) {
								// we forgot to clean up the buffer
								strbuf = new StringBuffer();
							}
							add = false;
						} else {
							add = true;
						}
					} else if (ch == '\\') { // escape character
						add = false;
						n = in.read();
						if (n > -1) {
							ch = (char) n;
							if (ch == '\n' || ch == '\r') {
								strbuf = new StringBuffer(strbuf.toString().trim());
								strbuf.append(' ');
								ch = (char) in.read();
								add = true;
							} else
								add = true;
						} else {
							finished = true;
						}
					} else if (state != STATE_VARIABLE_DEFINE && (ch == '#' || ch == '\n' || ch == '\r' || ch == '\0' || n == -1)) { // comment
																																		// or
																																		// endl
																																		// or
																																		// eof
						add = false;
						String value = null;
						if (start < index)
							value = strbuf.toString();
						String varID = stack.pop();
						if (state == STATE_VARIABLE_APPEND) {
							varManager.append(varID, value);
						} else {
							Variable var = new Variable(varID, value, override, false);
							if (expandNow)
								var.expand(varManager); // expand immediately :=
							varManager.addNew(var); // save the variable
						}
						if (override)
							stack.pop(); // pop out the "override" word
						if (ch == '#') {
							state = STATE_COMMENT;
						} else {
							state = STATE_NONE; // finished with this variable
						}
						// reset to defaults
						override = false;
						define = false;
						include = false;
						skipRead = false;
						leadSpace = true;
						cannotBeCommand = false;
						expandNow = false;
						strbuf = new StringBuffer();
					} else if (state == STATE_VARIABLE_DEFINE && (ch == '\n' || ch == '\r' || ch == '\0' || n == '\1')) {
						String varVal = strbuf.toString();
						if (varVal.trim().endsWith("endef")) {
							strbuf.delete(strbuf.lastIndexOf("endef") - 1, strbuf.length());
							skipRead = true;
							state = STATE_VARIABLE;
						} else {
							add = true;
						}
					} else { // alphanums
						if (leadSpace) { // so far we've been ignoring the
											// leading space
							start = index;
							leadSpace = false;
						}
					}
					if (add) {
						strbuf.append(ch);
					}

					break;

				case STATE_TARGET:
					add = true;
					if (ch == ' ') {
						add = false;
						if (leadSpace) {
							// this is leading space
						} else {
							// this is a separator between prerequisites
							if (strbuf.length() > 0) {
								if (!orderOnly)
									normalPrereqs.addFirst(strbuf.toString());
								else
									orderOnlyPrereqs.addFirst(strbuf.toString());
								strbuf = new StringBuffer();
							}
						}
					} else if (ch == '|') { // divider between normal and
											// order-only prereqs
						add = false;
						leadSpace = true;
						if (strbuf.length() > 0) {
							if (!orderOnly)
								normalPrereqs.addFirst(strbuf.toString());
							else
								orderOnlyPrereqs.addFirst(strbuf.toString());
							strbuf = new StringBuffer();
						}
						orderOnly = true;
					} else if (ch == '#' || ch == '\n' || ch == '\r' || ch == '\0') { // comment
																						// or
																						// endl
																						// or
																						// eof
						add = false;
						leadSpace = false;
						if (strbuf.length() > 0) {
							if (!orderOnly)
								normalPrereqs.addFirst(strbuf.toString());
							else
								orderOnlyPrereqs.addFirst(strbuf.toString());
							strbuf = new StringBuffer();
						}
						if (ch == '#') {
							state = STATE_COMMENT;
						} else {
							state = STATE_AFTER_TARGET; // finished with the
														// prereqs
						}

						// reset to defaults
						skipRead = false;
						leadSpace = true;
						cannotBeCommand = false;
					} else if (ch == ';') { // commands
						leadSpace = true;
						if (strbuf.length() > 0) {
							if (!orderOnly)
								normalPrereqs.addFirst(strbuf.toString());
							else
								orderOnlyPrereqs.addFirst(strbuf.toString());
							strbuf = new StringBuffer();
						}
						state = STATE_COMMAND;
					} else if (ch == '\\') { // escape char, or continue on next
												// line
						add = false;
						n = in.read();
						if (n > -1) {
							ch = (char) n;
							if (ch == '\n' || ch == '\r') {
								strbuf = new StringBuffer(strbuf.toString().trim());
								strbuf.append(' ');
								ch = (char) in.read();
								add = true;
							} else
								add = true;
						} else
							finished = true;
					} else {
						leadSpace = false;
					}

					if (add) {
						strbuf.append(ch);
					}
					break;

				case STATE_AFTER_TARGET:
					if (ch == '\t')
						state = STATE_COMMAND;
					else {
						state = STATE_NONE;
						skipRead = true;
						// at this point we need to save the target with its
						// commands, prereqs etc.
						Target prototype = new Target("prototype", uniqueName);
						while (!normalPrereqs.isEmpty()) {
							String prereq = normalPrereqs.peekLast();
							normalPrereqs.removeLast();
							if (!prereq.trim().equals(""))
								prototype.getNormalPrerequisites().add(prereq.trim());
						}
						while (!orderOnlyPrereqs.isEmpty()) {
							String prereq = orderOnlyPrereqs.peekLast();
							orderOnlyPrereqs.removeLast();
							if (!prereq.trim().equals(""))
								prototype.getOrderOnlyPrerequisites().add(prereq.trim());
						}
						while (!stack.isEmpty()) {
							String cmdLine = stack.pop();
							if (cmdLine != null && !cmdLine.trim().equals(""))
								prototype.getCommands().add(0, new Command(cmdLine));
						}
						// from the prototype, we create/append the other
						// commands
						while (!targetNames.isEmpty()) {
							String targetName = targetNames.pop();
							Target existing = findTarget(targetName);
							if (existing != null && existing.isUniqueName()) {
								String msg = "Target " + targetName + " defined more than once.";
								if (ErrorManager.get().shouldFail(msg, MakefileParser.class)) {
									throw new Error(msg);
								} else {
									targets.remove(existing); // override the
																// existing
								}
							}
							Target newTarget = new Target(targetName, uniqueName);
							for (String npr : prototype.getNormalPrerequisites()) {
								newTarget.getNormalPrerequisites().add(npr);
							}
							for (String oopr : prototype.getOrderOnlyPrerequisites()) {
								newTarget.getOrderOnlyPrerequisites().add(oopr);
							}
							for (Command cmd : prototype.getCommands()) {
								newTarget.getCommands().add(cmd);
							}
							targets.add(newTarget);
						}
						leadSpace = true;
					}
					break;

				case STATE_COMMAND:
					add = true;
					if (ch == '\n' || ch == '\r' || ch == '\0' || n == -1) {
						add = false;
						state = STATE_AFTER_TARGET;
						stack.push(strbuf.toString());
						strbuf = new StringBuffer();
					} else if (ch == '\\') { // escape char or newline
						add = false;
						n = in.read();
						if (n > -1) {
							ch = (char) n;
							if (ch == '\n' || ch == '\r') {
								strbuf = new StringBuffer(strbuf.toString().trim());
								strbuf.append(' ');
								ch = (char) in.read();
								add = true;
							} else
								add = true;
						} else
							finished = true;
					}
					if (add) {
						strbuf.append(ch);
					}
					break;

				} // end switch(state)
			}
		}

		in.close();
	}

	/**
	 * 
	 * @param in The data to read
	 * @return true if endif was found, false otherwise
	 * @throws IOException
	 */
	private boolean handleIfdefCondFalse(final Reader in) throws IOException {
		boolean elseReached = false;
		boolean endifReached = false;
		int n = 0;
		char ch = '\0';
		StringBuffer strbuf = new StringBuffer();
		int currIfdefCount = keywordCounts.ifdef_count;
		while (!elseReached && !endifReached) {
			while (ch != '\n' && ch != ' ') {
				n = in.read();
				if (n == -1) {
					return false;
				}
				ch = (char) n;
				strbuf.append(ch);
			}
			if (strbuf.toString().startsWith("ifdef") || strbuf.toString().startsWith("ifndef")) {
				keywordCounts.ifdef_count++;
			}

			else if (strbuf.toString().startsWith("endif")) {
				if (keywordCounts.ifdef_count == currIfdefCount)
					endifReached = true;
				keywordCounts.ifdef_count--;
			} else if (strbuf.toString().startsWith("else") && keywordCounts.ifdef_count == currIfdefCount) {

				elseReached = true;
			}
			strbuf = new StringBuffer();
			ch = '\0';
		}
		return endifReached;
	}

	private void discardRestOfIfdefBlock(final Reader in) throws IOException {
		boolean endIfReached = false;
		int n = 0;
		char ch = '\0';
		StringBuffer strbuf = new StringBuffer();
		while (!endIfReached) {
			while (ch != '\n') {
				n = in.read();
				if (n == -1) {
					return;
				}
				ch = (char) n;
				strbuf.append(ch);
			}

			if (strbuf.toString().startsWith("endif")) {
				keywordCounts.ifdef_count--;
				if (keywordCounts.ifdef_count == 0) {
					endIfReached = true;
					keywordCounts.resetCounters();
				}
			}
			if (strbuf.toString().startsWith("ifdef")) {
				keywordCounts.ifdef_count++;
			}
			strbuf = new StringBuffer();
			ch = '\0';
		}

	}

	/**
	 * Throw an error
	 * 
	 * @param index
	 *            at which char did the error occur
	 * @param found
	 *            what has been found there
	 * @param expected
	 *            what was expected to be found
	 * @throws ParseException
	 *             - this exception is thrown
	 */
	private void error(int index, char found, String expected) throws ParseException {
		String msg = "Error [" + index + "]: " + expected + " expected but " + found + " was found.";
		System.err.println(msg);
		throw new ParseException(msg, index);
	}

	/** Get the (unmodifiable) list of targets */
	public List<Target> getTargets() {
		return Collections.unmodifiableList(targets);
	}

	/** Fetch a target from the list, or return null */
	private Target findTarget(String name) {
		for (Target t : targets) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		return null;
	}

	/** Used internally for debugging purposes */
//	private String state(int state) {
//		switch (state) {
//		case STATE_COMMENT:
//			return "COMMENT";
//		case STATE_COMMENT_STACKED:
//			return "COMMENT_STACKED";
//		case STATE_ALPHANUM:
//			return "ALPHANUM";
//		case STATE_VARIABLE:
//			return "VARIABLE";
//		case STATE_VARIABLE_APPEND:
//			return "VARIABLE_APPEND";
//		case STATE_VARIABLE_DEFINE:
//			return "VARIABLE_DEFINE";
//		case STATE_TARGET:
//			return "TARGET";
//		case STATE_AFTER_TARGET:
//			return "AFTER_TARGET";
//		case STATE_COMMAND:
//			return "COMMAND";
//		default:
//			return "NONE";
//		}
//	}

	public String toString() {
		StringBuffer res = new StringBuffer(300);
		res.append("[Makefile Parser]\n");
		res.append("[Targets]\n");
		for (Target t : targets) {
			res.append(t.toString()).append('\n');
		}
		res.append("\n[Variables]\n");
		Iterator<String> varNames = varManager.nonExternalKeys();
		while (varNames.hasNext()) {
			String varName = varNames.next();
			String value = varManager.getValue(varName);
			res.append(varName).append(" = ").append(value).append('\n');
		}

		return res.toString();
	}

	/** Create a shallow copy (clone) of the Parser */
	public Object clone() throws CloneNotSupportedException {
		MakefileParser clone = new MakefileParser(varManager);
		clone.targets = targets;
		return clone;
	}

	private static class Counters {

		int ifdef_count = 0;

		private void resetCounters() {
			ifdef_count = 0;

		}
	}
	/**
	 * 
	 */
	public void getDefines(HashMap<String,String> defines) {
		Pattern pattern;
		Matcher matcher1,matcher2,matcher3,matcher4;
		String patNameValue = "([\\S]*?='.*?')";
		String papValue = ".*='(.)*'+";
		String patName = "(.+)='.+'";
		String patCFlags = "-D([\\S]*)(\\s)*";
		String patArch;
		
		String def,val;
		// Search for defines in makefile
		Iterator<String> varNames = varManager.nonExternalKeys();
		while (varNames.hasNext()) {
			String varName = varNames.next();
			String value = varManager.getValue(varName);
			// Search for name define
			if (varName.equals("DEFINES")) {
				pattern = Pattern.compile(patNameValue);
				matcher1 = pattern.matcher(value);
				while(matcher1.find()) {
					def = matcher1.group(1);
					// Find value
					pattern = Pattern.compile(papValue);
					matcher2 = pattern.matcher(def);
					if (matcher2.find()) {
						val = matcher2.group(1);
						// Find name
						pattern = Pattern.compile(patName);
						matcher2 = pattern.matcher(def);
						if (matcher2.find()) {
							def = matcher2.group(1);
							defines.put(def,val);
						}
					}
				}
			}
			// Search for OWN_C_FLAGS
			if (varName.equals("OWN_CFLAGS")) {
				pattern = Pattern.compile(patCFlags);
				matcher3 = pattern.matcher(value);
				while(matcher3.find()) {
					def = matcher3.group(1);
					pattern = Pattern.compile(papValue);
					matcher2 = pattern.matcher(def);
					if (matcher2.find()) {
						val = matcher2.group(1);
					} else {
						val = "1";
					}
					defines.put(def,val);
				}				
			}
			// Search for ARCH (and DEBUG,RELEASE, STD_IO_USIF, ...)
			if (varName.equals("ARCH")) {
				// Project TODO could be missing from ARCH define
				patArch = "(XGOLD[0-9]+)";
				pattern = Pattern.compile(patArch);
				matcher4 = pattern.matcher(value);
				if (matcher4.find()) {
					defines.put(matcher4.group(1),"1");
				}
				// USIF
				patArch = "(STD[_A-Z]+)";
				pattern = Pattern.compile(patArch);
				matcher4 = pattern.matcher(value);
				if (matcher4.find()) {
					defines.put(matcher4.group(1),"1");
				}
			}
		}		
		// Add default define
	}
	/**
	 * Retrieve all source directories from SRC, SRCDIR and VPATH define
	 * @param sourceDir An HashSet of String containing all source directories
	 */
	public void getSourceDir(Set<String> sourceDir) {
		Pattern pattern;
		Matcher matcher;
		String pat = "([/\\.\\\\\\w]+)[\\s$]*";
		
		Iterator<String> varNames = varManager.nonExternalKeys();
		while (varNames.hasNext()) {
			String varName = varNames.next();
			String value = varManager.getValue(varName);
			if (varName.equals("SRC")) {
				pattern = Pattern.compile(pat);
				matcher = pattern.matcher(value);
				while (matcher.find()) {
					String s = matcher.group(1);
					int pos = s.lastIndexOf("\\");
					if (pos != -1) {
						sourceDir.add((s.substring(0,pos-1)).trim());
					} else {
						pos = s.lastIndexOf("/");
						if (pos != -1) {
							sourceDir.add((s.substring(0,pos-1)).trim());
						} 
					}
				}
			} else if (varName.equals("SRCDIR")) {
				pattern = Pattern.compile(pat);
				matcher = pattern.matcher(value);
				while (matcher.find()) {
					String s = matcher.group(1);
					sourceDir.add(s.trim());
				}
			} else if (varName.equals("VPATH")) {
				pattern = Pattern.compile(pat);
				matcher = pattern.matcher(value);
				while (matcher.find()) {
					String s = matcher.group(1);
					sourceDir.add(s.trim());
				}
			}
		}
		System.out.println("Source dir to be added: "+sourceDir);
	}
	/**
	 * Retrieve all include directories from INCDIR define
	 * @param includeDir An HashSet of String containing all include directories
	 */
	public void getIncludeDir(Set<String> includeDir) {
		Pattern pattern;
		Matcher matcher;
		String pat = "([/\\.\\\\\\w]+)[\\s$]*";
		
		Iterator<String> varNames = varManager.nonExternalKeys();
		while (varNames.hasNext()) {
			String varName = varNames.next();
			String value = varManager.getValue(varName);
			if (varName.equals("INCDIR")) {
				pattern = Pattern.compile(pat);
				matcher = pattern.matcher(value);
				while (matcher.find()) {
					String s = matcher.group(1);
					includeDir.add(s.trim());
				}
			}
		}
		System.out.println("Include dir to be added: "+includeDir);
	}
	
	public static void main(String args[]) {
		String fileLocation = "M:\\dev_xg223_es1_gautier\\CRYPTO\\S-GOLD_Family_Environment\\Testcases\\CRYPTO_test\\CRYPTO_TC_Ciph3DES\\makefile";
//		String fileLocation = "M:\\dev_ets_xg223_gautier\\S-Gold\\S-GOLD_Family_Environment\\Testcases\\DMA_test\\DMA_test\\makefile";		
//		String fileLocation = "C:\\Tmp\\TC2\\makefile";		
		VariableManager var = new VariableManager();
		MakefileParser parMake = new MakefileParser(var);
		try {
			parMake.parse(new File(fileLocation));
			System.out.println(parMake);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Set<String> s = new HashSet<String>();
		Set<String> i = new HashSet<String>();
		HashMap<String,String> defines = new HashMap<String, String>();
		parMake.getSourceDir(s);	
		parMake.getIncludeDir(i);	
		parMake.getDefines(defines);
	}
}
