package com.xb.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.reggie.common.CustomException;
import com.xb.reggie.entity.Category;
import com.xb.reggie.entity.Dish;
import com.xb.reggie.entity.Setmeal;
import com.xb.reggie.mapper.CategoryMapper;
import com.xb.reggie.service.CategoryService;
import com.xb.reggie.service.DishService;
import com.xb.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    @Override
    public void removeById(Long id) {
        LambdaQueryWrapper<Dish> dishQuery = new LambdaQueryWrapper<>();
        dishQuery.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishQuery);
        if(count > 0) {
            throw new CustomException("关联菜品还存在，无法删除");
        }

        LambdaQueryWrapper<Setmeal> setmealQuery = new LambdaQueryWrapper<>();
        setmealQuery.eq(Setmeal::getCategoryId, id);
        count = setmealService.count(setmealQuery);
        if(count > 0) {
            throw new CustomException("关联套餐还存在，无法删除");
        }
        super.removeById(id);
    }
}
