package com.gsoft.cos3.util.excel;

public class ColumnVO
{
    /**
     * 列字段名称
     */
    private String field;
    
    /**
     * 列标题文本
     */
    private String title;
    
    /**
     * 指明将占用多少行单元格（合并行）
     */
    private Integer rowspan;

    /**
     * 指明将占用多少列单元格（合并列）
     */
    private Integer colspan;

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public Integer getRowspan()
    {
        return rowspan;
    }

    public void setRowspan(Integer rowspan)
    {
        this.rowspan = rowspan;
    }

    public Integer getColspan()
    {
        return colspan;
    }

    public void setColspan(Integer colspan)
    {
        this.colspan = colspan;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}
