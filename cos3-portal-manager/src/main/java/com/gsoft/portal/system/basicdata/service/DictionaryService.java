package com.gsoft.portal.system.basicdata.service;


import com.gsoft.portal.system.basicdata.dto.DictionaryDto;
import com.gsoft.portal.system.basicdata.dto.DictionaryItemDto;

import java.util.List;

/**
 * 数据字典服务接口
 *
 * @author plsy
 * @Date 2017年8月11日 下午2:10:37
 */
public interface DictionaryService {

    /**
     * 查询所有分类
     *
     * @return
     */
    List<String> getTypes();

    /**
     * 查询所有参数
     *
     * @return
     */
    List<DictionaryDto> getList();

    /**
     * 根据type查询参数
     *
     * @return
     */
    List<DictionaryDto> getListByType(String type);

    /**
     * 保存一个数据字典项
     *
     * @param dictionaryDto
     * @return
     */
    DictionaryDto save(DictionaryDto dictionaryDto);


    /**
     * 删除数据字典项
     *
     * @param id
     */
    void removeDictionary(Long id);

    /**
     * 保存数据字典项
     *
     * @param dictionaryItemDto
     * @return
     */
    DictionaryItemDto saveDicItem(DictionaryItemDto dictionaryItemDto);

    /**
     * 删除数据字典项
     *
     * @param id
     */
    void removeDictionaryItem(Long id);

    /**
     * 通用的根据数据字典键获取数据库项集合
     *
     * @param key
     * @return
     */
    List<DictionaryItemDto> getDicItemsByKey(String key);


    /**
     * 通过name得到数据字典集合
     *
     * @param name
     * @return
     */
    List<DictionaryDto> getListByName(String name);

    /**
     * 通过value得到数据字典项集合
     *
     * @param value
     * @return
     */
    List<DictionaryItemDto> getDicItemByValue(String value);

    /**
     * 验证是否存在重复的key
     *
     * @param id
     * @param key
     * @return
     */
    Boolean isExistsParmKey(Long id, String key);

    /**
     * 数据字典项验重
     *
     * @param id
     * @param value
     * @return
     */
    Boolean isExistsDictionaryItemValue(Long id, String dicKey, String value);

    /**
     *  根据dicKey和value获取ItemValue值
     *  [功能详细描述]
     * @param [参数1]     [参数1说明]
     * @param [参数2]     [参数2说明]
     * @return [返回类型说明]
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    DictionaryItemDto getDictionaryItemValue(String dicKey, String value);
}
