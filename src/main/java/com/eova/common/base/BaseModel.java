/**
 * Copyright (c) 2019 EOVA.CN. All rights reserved.
 * <p>
 * Licensed to: 卢中强(495993331@qq.com)
 * Licensed under the EPPL license: http://eova.cn/eppl.txt
 * Software copyright registration number:2018SR1012969
 * For authorization, please contact: admin@eova.cn
 */
package com.eova.common.base;

import com.eova.common.utils.xx;
import com.eova.config.EovaConst;
import com.jfinal.plugin.activerecord.*;

import java.util.List;

@SuppressWarnings("rawtypes")
public class BaseModel<M extends Model> extends Model<M> {

    private static final long serialVersionUID = 1702469565872353932L;

    @Override
    public List<M> findByCache(String cacheName, Object key, String sql) {
        // Base数据缓存30Min
        // if (sql.contains("from base_")) {
        // cacheName = BaseCache.BASE;
        // }
        return super.findByCache(cacheName, key, sql);
    }

    @Override
    public List<M> findByCache(String cacheName, Object key, String sql, Object... paras) {
        return super.findByCache(cacheName, key, sql, paras);
    }

    /**
     * 根据主键获取对象
     *
     * @param id 主键
     * @return
     */
    @SuppressWarnings("unchecked")
    public M getByCache(Object id) {
        String key = this.getClass().getSimpleName() + "_" + id;
        // get by cache
        M me = (M) BaseCache.getSer(key);
        if (me == null) {
            // get by db
            me = super.findById(id);
            if (me != null) {
                // add to service cache
                BaseCache.putSer(key, me);
            }
        }
        return me;
    }

    /**
     * 查询自动缓存
     *
     * @param sql
     * @return
     */
    public List<M> queryByCache(String sql) {
        // 查询SQL作为Key值
        return findByCache(BaseCache.SER, sql, sql);
    }

    /**
     * 查询自动缓存
     *
     * @param sql
     * @param paras
     * @return
     */
    public List<M> queryByCache(String sql, Object... paras) {
        // sql_xx_xx_xx
        String key = sql;
        for (Object obj : paras) {
            key += "_" + obj.toString();
        }
        return findByCache(BaseCache.SER, key, sql, paras);
    }

    /**
     * 缓存查询第一项
     *
     * @param sql
     * @return
     */
    public M queryFisrtByCache(String sql) {
        List<M> list = queryByCache(sql);
        if (xx.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 缓存查询第一项
     *
     * @param sql
     * @param paras
     * @return
     */
    public M queryFisrtByCache(String sql, Object... paras) {
        List<M> list = queryByCache(sql, paras);
        if (xx.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 缓存分页查询
     *
     * @param pageNumber      页码
     * @param pageSize        数量
     * @param select          查询前缀
     * @param sqlExceptSelect 查询条件
     * @return
     */
    public Page<M> pagerByCache(int pageNumber, int pageSize, String select, String sqlExceptSelect) {
        String key = select + sqlExceptSelect + "_" + pageNumber + "_" + pageSize;
        return super.paginateByCache(BaseCache.SER, key, pageNumber, pageSize, select, sqlExceptSelect);
    }

    /**
     * 缓存分页查询
     *
     * @param pageNumber      页码
     * @param pageSize        数量
     * @param select          查询前缀
     * @param sqlExceptSelect 查询条件
     * @param paras           SQL参数
     * @return
     */
    public Page<M> pagerByCache(int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) {
        String key = select + sqlExceptSelect + "_" + pageNumber + "_" + pageSize;
        for (Object obj : paras) {
            key += "_" + obj.toString();
        }
        return super.paginateByCache(BaseCache.SER, key, pageNumber, pageSize, select, sqlExceptSelect, paras);
    }

    /**
     * 是否存在
     *
     * @param sql
     * @param paras
     * @return
     */
    public boolean isExist(String sql, Object... paras) {
        String configName = DbKit.getConfig(this.getClass()).getName();
        Long count = Db.use(configName).queryNumber(sql, paras).longValue();
        return count != null && count != 0;
    }

    /**
     * 保存
     */
    @Override
    public boolean save() {
        String pk = null;
        if (xx.isOracle()) {
            Table table = TableMapping.me().getTable(getClass());
            pk = table.getPrimaryKey()[0];
            // 序列默认值
            if (this.get(pk) == null) {
                this.set(pk, EovaConst.SEQ_ + table.getName() + ".nextval");
            }
        }
        boolean isSave = super.save();
        if (xx.isOracle()) {
            this.set(pk, this.get(pk));
        }
        return isSave;
    }
}