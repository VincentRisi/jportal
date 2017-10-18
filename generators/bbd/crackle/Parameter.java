/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// 
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/*
 * Created on Jan 9, 2004
 */
package bbd.crackle;

import java.util.Vector;

/**
 * @author vince
 */
public class Parameter {
	public static final byte JAVA_BASED = 0, OBJC_BASED = 1, CPP_BASED = 2,
			BINU_BASED = 3;
	public static byte language;
	public String type;
	public String name;
	public Field field;
	// public boolean hasResult;
	public boolean isArray;
	public Action input;
	public int inputNo;
	public boolean isInput;
	public boolean hasInputSize;
	public String inputSizeValue;
	public Action output;
	public int outputNo;
	public boolean isOutput;
	public boolean hasOutputSize;
	public String outputSizeValue;

	public Parameter() {
		inputNo = -1;
		outputNo = -1;
		language = JAVA_BASED;
	}

	public String fullType() {
		if (field.type.reference == Type.ARRAYED)
			return type + "[]";
		return type + (isArray ? "[]" : "");
	}

	public static boolean build(Vector<Parameter> list, Prototype prototype,
			boolean keepTee) 
	{
		return build(list, prototype, keepTee, false);
	}

	public static boolean build(Vector<Parameter> list, Prototype prototype,
			boolean keepTee, boolean keepCase) 
	{
		list.removeAllElements();
		Parameter pd = null;
		boolean hasReturn = false;
		if (prototype.type.reference == Type.BYVAL
				&& prototype.type.typeof != Type.VOID)
			hasReturn = true;
		if (prototype.type.reference == Type.ARRAYED
				&& prototype.type.typeof != Type.VOID)
			hasReturn = true;
		for (int i = 0; i < prototype.parameters.size(); i++) {
			pd = new Parameter();
			Field field = (Field) prototype.parameters.elementAt(i);
			pd.field = field;
			for (int j = 0; j < prototype.inputs.size(); j++) {
				pd.input = (Action) prototype.inputs.elementAt(j);
				if (pd.input.name.compareTo(field.name) == 0) {
					pd.isInput = true;
					pd.inputNo = j;
					pd.hasInputSize = pd.input.hasSize();
					pd.inputSizeValue = pd.input.getSizeName();
					break;
				}
			}
			for (int j = 0; j < prototype.outputs.size(); j++) {
				pd.output = (Action) prototype.outputs.elementAt(j);
				if (pd.output.name.compareTo(field.name) == 0) {
					pd.isOutput = true;
					pd.outputNo = j;
					pd.hasOutputSize = pd.output.hasSize();
					pd.outputSizeValue = pd.output.getSizeName();
					break;
				}
			}
			pd.type = dropTee(languageName(field.type), keepTee);
			if (keepCase == true)
				pd.name = field.name;
			else
				pd.name = lowerFirst(field.name);
			if (field.type.reference == Type.BYREFPTR
					&& pd.type.compareTo("String") != 0)
				pd.isArray = true;
			else if ((pd.hasInputSize || pd.hasOutputSize)
					&& pd.type.compareTo("String") != 0)
				pd.isArray = true;
			list.addElement(pd);
		}
		return hasReturn;
	}

	public static boolean buildBinu(Vector<Parameter> list, Prototype prototype) {
		list.removeAllElements();
		Parameter pd = null;
		boolean hasReturn = false;
		if (prototype.type.reference == Type.BYVAL
				&& prototype.type.typeof != Type.VOID)
			hasReturn = true;
		if (prototype.type.reference == Type.ARRAYED
				&& prototype.type.typeof != Type.VOID)
			hasReturn = true;
		for (int i = 0; i < prototype.parameters.size(); i++) {
			pd = new Parameter();
			Field field = (Field) prototype.parameters.elementAt(i);
			pd.field = field;
			for (int j = 0; j < prototype.inputs.size(); j++) {
				pd.input = (Action) prototype.inputs.elementAt(j);
				if (pd.input.name.compareTo(field.name) == 0) {
					pd.isInput = true;
					pd.inputNo = j;
					pd.hasInputSize = pd.input.hasSize();
					pd.inputSizeValue = pd.input.getSizeName();
					break;
				}
			}
			for (int j = 0; j < prototype.outputs.size(); j++) {
				pd.output = (Action) prototype.outputs.elementAt(j);
				if (pd.output.name.compareTo(field.name) == 0) {
					pd.isOutput = true;
					pd.outputNo = j;
					pd.hasOutputSize = pd.output.hasSize();
					pd.outputSizeValue = pd.output.getSizeName();
					break;
				}
			}
			pd.type = languageName(field.type);
			pd.name = lowerFirst(field.name);
			if (field.type.reference == Type.BYREFPTR
					&& pd.type.compareTo("String") != 0)
				pd.isArray = true;
			else if ((pd.hasInputSize || pd.hasOutputSize)
					&& pd.type.compareTo("String") != 0)
				pd.isArray = true;
			list.addElement(pd);
		}
		return hasReturn;
	}

	public static String languageName(Type type) {
		switch (language) {
		case JAVA_BASED:
			return type.javaName();
		case OBJC_BASED:
			return type.objcName();
		case CPP_BASED:
			return type.cName();
		case BINU_BASED:
			return type.binuName();
		}
		return type.name;
	}

	public static String lowerFirst(String in) {
		String result = in.substring(0, 1).toLowerCase() + in.substring(1);
		return result;
	}

	public static String dropTee(String in, boolean keepTee) {
		String result = in;
		if (in.compareTo("char") == 0) {
			result = "String";
		} else if (keepTee == false) {
			if (result.charAt(0) == 't')
				result = result.substring(1);
		}
		return result;
	}
}
