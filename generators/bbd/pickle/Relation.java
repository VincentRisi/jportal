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
package bbd.pickle;

import java.io.Serializable;
import java.util.Vector;

/**
 * Relation identified by name holds fields, keys, links, grants, views and
 * procedures associated with the table.
 */
public class Relation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5192003500363466281L;
	public Application application;
	public String name;
	public String descr;
	public String alias;
	public String fromShort;
	public String toShort;
	public Table fromTable;
	public Table toTable;
	public Vector<Field> fromFields;
	public Vector<Field> toFields;
	public Vector<String> fromFieldNames;
	public Vector<String> toFieldNames;
	public Vector<String> comments;
	public Vector<Value> values;
	public Validation validation;

	public Relation() {
		name = "";
		descr = "";
		alias = "";
		fromShort = "";
		toShort = "";
		fromFields = new Vector<Field>();
		toFields = new Vector<Field>();
		fromFieldNames = new Vector<String>();
		toFields = new Vector<Field>();
		toFieldNames = new Vector<String>();
		comments = new Vector<String>();
		values = new Vector<Value>();
		validation = new Validation();
	}

	public String useName() {
		if (alias.length() > 0)
			return alias;
		return name;
	}
}
