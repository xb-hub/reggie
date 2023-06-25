package com.xb.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.reggie.dto.SetmealDto;
import com.xb.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveByDto(SetmealDto setmealDto);
    public void updateByDto(SetmealDto setmealDto);
    public void deleteDtoById(List<Long> ids);
}
