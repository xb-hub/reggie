package com.xb.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.reggie.dto.DishDto;
import com.xb.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    public void saveDto(DishDto dishDto);

    public DishDto getDtoById(Long id);

    public void updateDto(DishDto dishDto);

    public void deleteDtoById(List<Long> ids);
}
