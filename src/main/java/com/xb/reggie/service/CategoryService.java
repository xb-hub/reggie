package com.xb.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public void removeById(Long id);
}
