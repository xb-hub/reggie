package com.xb.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.reggie.common.R;
import com.xb.reggie.dto.SetmealDto;
import com.xb.reggie.entity.Dish;
import com.xb.reggie.entity.Setmeal;
import com.xb.reggie.entity.SetmealDish;
import com.xb.reggie.mapper.SetmealMapper;
import com.xb.reggie.service.SetmealDishService;
import com.xb.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    SetmealService setmealService;

    @Override
    public void saveByDto(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        for (SetmealDish iter : setmealDishList) {
            iter.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishList);
    }

    @Override
    public void updateByDto(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        Long setmealId = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(queryWrapper);
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        for (SetmealDish iter : setmealDishList) {
            iter.setSetmealId(setmealId);
        }
        setmealDishService.saveBatch(setmealDishList);
    }

    @Override
    public void deleteDtoById(List<Long> ids) {
        for (Long id : ids) {
            this.removeById(id);
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId, id);
            setmealDishService.remove(queryWrapper);
        }
    }
}
