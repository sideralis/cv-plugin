package com.infineon.cv.wizard;

public class IntelProjectTemplate {
	public static final String makefileTestcase = new String(
			  "# See http://wiki.intra.infineon.com/CV_compilation_flow for more information. \n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# Specify the name of your project.\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "PROJECT_NAME := TESTCASE_NAME\n"
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# Specify the short name of your project. If empty, PROJECT_NAME will be used\n"
			+ "# Otherwise, it will be used as name for the object files (.bin,.hex,.axf,.ibi)\n" 
			+ "#------------------------------------------------------------------------------\n"
			+ "PROJECT_SHORT_NAME :=\n" 
			+ "\n" 
			+ "#------------------------------------------------------------------------------\n"
			+ "# Specify the default architecture (type gnumake list in the cmd line for help)\n" 
			+ "#------------------------------------------------------------------------------\n"
			+ "ARCH := REAL+RAM+STD_IO_USIF+DEBUG+GENERIC_SDRAM_AND_NO_NVM\n" 
			+ "\n" 
			+ "#------------------------------------------------------------------------------\n"
			+ "# Specify the location of your S-Gold_sofware_environment folder\n" 
			+ "#------------------------------------------------------------------------------\n" 
			+ "ROOTDIR := ROOT_DIR\n" 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# Specify the folder where object files (.bin,.hex,.axf,.ibi) should be stored\n"
			+ "# If empty, default folder is makefile location folder\n" 
			+ "#------------------------------------------------------------------------------\n" 
			+ "EXEC_PATH :=\n" 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# Specify below your own scatter file if needed\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "MY_SCF :=\n" 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# Specify below your own initialization file if needed\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "OWN_INIT_S :=\n" 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# Specify below your own assembler flags if needed\n"
			+ "# WARNING : Keep in mind to get out compilation flags which have to be replaced\n" 
			+ "# by your own flags... by instance use $(filter-out OLD_FLAGS,$(ASFLAGS))\n"
			+ "# OWN_ASFLAGS 	-> Assembler flags\n" + "# OWN_CFLAGS	-> C compiler flags\n" 
			+ "# OWN_LKFLAGS	-> Linker flags\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "OWN_ASFLAGS  :=\n" 
			+ "OWN_CFLAGS   :=\n" 
			+ "OWN_LKFLAGS  :=\n" 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# All C and assembler defines can be defined in this variable\n"
			+ "# ! Use always NAME='VALUE' pattern\n" 
			+ "#------------------------------------------------------------------------------\n" 
			+ "DEFINES := #TOTO='1' ADDRESS='0x8000'\n" 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# By default, the TCM is not used.\n" 
			+ "# In case of it has to be used :\n"
			+ "#			- Hard code the address value of ITCM,DTCM in the current makefile\n" 
			+ "#			- Set theirs variable values in the command line\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "ITCM_BASE_ADDRESS =\n" 
			+ "DTCM_BASE_ADDRESS =\n" 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# List the files to compile for your testcase\n"
			+ "# Either by specifying them one by one (use SRCS) or\n"
			+ "# by specifying directories (use SRCDIR). In this case all source files (.c,.s) located in the folder are automatically compiled\n"
			+ "# VPATH is used to specify which folders will be searched in case the source name file\n" 
			+ "# does not include a full path\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "SRCS += TESTCASE_NAME.c\n" 
			+ "SRCDIR +=\n" 
			+ "VPATH += " 
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# You can add one or more folders in the include path\n"
			+ "# These are the paths needed by the compiler to resolve all include instructions\n" 
			+ "#------------------------------------------------------------------------------\n" 
			+ "#INCDIR +=\n"
			+ "\n" 
			+ "#------------------------------------------------------------------------------\n" 
			+ "# You must list the source folder of the libraries requested for your test\n"
			+ "# Hades library is included by default\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "LIBRARIES= $(ROOTDIR)/_lib/_src/_lib_CGU \\\n"
	        + "           $(ROOTDIR)/_lib/_src/_lib_USIF \\\n"
	        + "           $(ROOTDIR)/_lib/_src/_lib_ICU \\\n"
	        + "           $(ROOTDIR)/_lib/_src/_lib_LPDDR2_MC \\\n"
	        + "           $(ROOTDIR)/_lib/_src/_lib_TEST\n"
			+ "\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "# Call the main makefile\n"
			+ "#------------------------------------------------------------------------------\n" 
			+ "#include $(ROOTDIR)/_makefile/Makefile.mak\n" 
			+ "include $(ROOTDIR)/../../CV_Foundation/_makefile/Makefile.mak\n" 
			+ "");
	
	
	public final static String cFileTestcase = new String(
			"/*\n" + 
			" * TESTCASE_NAME.c\n" + 
			" *\n" + 
			" *  Created on: DD MM YYYY\n" + 
			" *      Author: USERNAME\n" + 
			" */\n" + 
			"\n" + 
			"#include <stdio.h>\n" + 
			"#include <STD_stdio.h>\n" + 
			"\n" + 
			"#include <REG_SGOLD.h>\n" + 
			"\n" + 
			"#include <CGU_lib.h>\n" + 
			"#include <TEST_lib.h>\n" + 
			"#include <HADES_lib.h>\n" + 
			"\n" + 
			"/**\n" + 
			" * Main function\n" + 
			" * @return always 0.\n" + 
			" */\n" + 
			"int main(void) {\n" + 
			"	int ret = 0;\n" + 
			"\n" + 
			"	// Configure clock to fast clock settings\n" + 
			"	CGU_ConfAllClocks(FAST_CLOCKS_SETTINGS);\n" + 
			"\n" + 
			"	// Initialise USIF port\n" + 
			"	STD_IO_Init(STD_IO_115200);\n" + 
			"\n" + 
			"	// Fill the Hades data used for report\n" + 
			"	HADES_TestQCSignature(\"Test name in QC\",\"Block name in QC\",\"Description of your test\");\n" + 
			"\n" + 
			"	// TODO Add the code of your test\n" + 
			"	// ...\n" + 
			"\n" + 
			"	// Send the pass or fail status of your test.\n" + 
			"	TEST_sendTcVerdict(TEST_PASS);\n" + 
			"\n" + 
			"	// Return ret\n" + 
			"	return ret;\n" + 
			"}\n" + 
			"");
}
