package com.gsoft.cos3.table;

import java.util.HashMap;
import java.util.Map;

public class Constant {

	public static final String PARAM_NAME_ORDER = "order";
	public static final String PARAM_NAME_SORT = "sort";

	public static final String KEY_RESULT = "result";

	public static final String TREE_NODE_ID = "id";
	public static final String TREE_NODE_PARENT_ID = "parentId";
	public static final String TREE_NODE_TEXT = "text";
	public static final String TREE_NODE_LABEL = "label";
	public static final String TREE_NODE_STATE = "state";
	public static final String TREE_NODE_CHECKED = "checked";
	public static final String TREE_NODE_ICON_CLASS_NAME = "iconCls";
	public static final String TREE_NODE_CASCADE = "cascade";
	public static final String TREE_NODE_CODE = "code";
	
	public static final String COLUMN_NAME_PARENT_ID = "C_PARENT_ID";
	public static final String COLUMN_NAME_TEXT = "C_TEXT";
	public static final String COLUMN_NAME_STATE = "C_STATE";
	public static final String COLUMN_NAME_CHECKED = "C_CHECKED";
	public static final String COLUMN_NAME_ICON_CLASS_NAME = "C_ICON";
	public static final String COLUMN_NAME_ICON_CLASS = "C_ICON_CLS";
	public static final String COLUMN_NAME_ID = "C_ID";
	public static final String COLUMN_NAME_NAME = "C_NAME";
	public static final String COLUMN_NAME_CREATE_TIME = "C_CREATE_TIME";
	public static final String COLUMN_NAME_CREATEBY = "C_CREATE_BY";
	public static final String COLUMN_NAME_CREATOR_ACCOUNT_NAME = "C_CREATOR_ACCOUNT_NAME";
	public static final String COLUMN_NAME_CREATOR_ID = "C_CREATOR_ID";
	public static final String COLUMN_NAME_CREATOR_NAME = "C_CREATE_NAME";
	public static final String COLUMN_NAME_UPDATE_TIME = "C_UPDATE_TIME";
	public static final String COLUMN_NAME_UPDATER_ACCOUNT_ID = "C_UPDATE_BY";
	public static final String COLUMN_NAME_UPDATER_ACCOUNT_NAME = "C_UPDATER_ACCOUNT_NAME";
	public static final String COLUMN_NAME_UPDATER_ID = "C_UPDATER_ID";
	public static final String COLUMN_NAME_UPDATER_NAME = "C_UPDATER_NAME";
	public static final String COLUMN_NAME_DELETED = "C_DELETED";
	public static final String COLUMN_NAME_DISABLED = "C_DISABLED";
	public static final String COLUMN_NAME_SORT = "C_SORTNO";
	public static final String COLUMN_NAME_YEAR = "C_YEAR";
	public static final String COLUMN_CASCADE = "C_CASCADE";
	public static final String COLUMN_CASCADE_ID = "C_CASCADE_ID";
	public static final String COLUMN_NAME_CODE = "C_VALUE";
	//以下是树形结构新增属性
	public static final String COLUMN_NAME_PARENT_PATH = "C_PARENT_PATH";
	public static final String COLUMN_NAME_LEVEL = "C_LEVEL";
	
	public static final String TABLES_TABLE_NAME = "TBL_TABLES";
	public static final String JSP_TEMPLATE_TABLE_NAME = "TBL_JSP_TEMPLATES";
	public static final Map<String, TableDefinition> DEFAULT_TABLE_DEFINITIONS = new HashMap<String, TableDefinition>();

	static{
		DEFAULT_TABLE_DEFINITIONS.put(TABLES_TABLE_NAME, new TableDefinition());
	}

	public static final String COLUMN_NAMES = "columnNames";
	
	
	/**
	 * appfactory 模板类型
	 * xiaotao
	 * @return
	 */
	public static class AppFacoryType{

		public static final String app_4_form = "app_4_form";

		public static final String app_4_table = "app_4_table";
		
		public static final String app_4_tree = "app_4_tree";
		
	}
}
