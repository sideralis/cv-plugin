package com.infineon.cv.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ScatterScanner extends RuleBasedScanner {
	private static RGB COMMENT = new RGB(128, 0, 0);
	private static RGB KEYWORD= new RGB(0, 0, 128);
	private static RGB DEFAULT = new RGB(0,0,0);
	private static RGB ADDRESS = new RGB(0,128,0);
	 
	public ScatterScanner() {
		super();
		setRules(extractRules());
	}
 
	private IRule[] extractRules() {
		IToken keyword = new Token(new TextAttribute(new Color(Display.getCurrent(), KEYWORD), null, SWT.BOLD));
		IToken address = new Token(new TextAttribute(new Color(Display.getCurrent(), ADDRESS), null, SWT.BOLD));
		IToken comment = new Token(new TextAttribute(new Color(Display.getCurrent(), COMMENT), null, SWT.ITALIC));
		IToken defaut = new Token(new TextAttribute(new Color(Display.getCurrent(), DEFAULT)));

		IRule [] rules = new IRule[3];
		
		// To detect keywords
		WordRule ruleKeyword = new WordRule(new IWordDetector() {
			@Override
			public boolean isWordStart(char c) {
				if (Character.isLetter(c))
					return true;
				else
					return false;
			}
			@Override
			public boolean isWordPart(char c) {
				return Character.isJavaIdentifierPart(c);
			}
 
		}, defaut);
 
		for (String k : ScatterKeywords.KEYWORDS) {
            ruleKeyword.addWord(k, keyword);
		}

		// To detect addresses
		WordRule ruleAddress = new WordRule(new IWordDetector() {
			
			@Override
			public boolean isWordStart(char c) {
				return Character.isDigit(c);
			}
			
			@Override
			public boolean isWordPart(char c) {
				if (c=='x' || c=='X' || (c>='0' && c<='9') || (c>='a' && c<='f') || (c>='A' && c<='F'))
					return true;
				else
					return false;
			}
		},address);
		
		rules[0]=ruleKeyword;
		rules[1]=ruleAddress;
		rules[2]=new SingleLineRule(";", null, comment);
 
		return rules;
	}

}
