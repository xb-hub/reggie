package com.xb.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.reggie.dto.DishDto;
import com.xb.reggie.entity.Dish;
import com.xb.reggie.entity.DishFlavor;
import com.xb.reggie.mapper.DishMapper;
import com.xb.reggie.service.DishFlavorService;
import com.xb.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;
    @Override
    public void saveDto(DishDto dishDto) {
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavorList = dishDto.getFlavors();
        for (DishFlavor iter : flavorList) {
            iter.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavorList);
    }

    @Override
    public DishDto getDtoById(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        Long dishId = dish.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishId);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Override
    public void updateDto(DishDto dishDto) {
        this.updateById(dishDto);
        Long dishId = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavorList = dishDto.getFlavors();
        for (DishFlavor iter : flavorList) {
            iter.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavorList);
    }

    @Override
    public void deleteDtoById(List<Long> ids) {
        for (Long id : ids) {
            this.removeById(id);
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, id);
            dishFlavorService.remove(queryWrapper);
        }
    }


}
