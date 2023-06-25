package com.xb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xb.reggie.common.R;
import com.xb.reggie.dto.DishDto;
import com.xb.reggie.dto.SetmealDto;
import com.xb.reggie.entity.Category;
import com.xb.reggie.entity.Dish;
import com.xb.reggie.entity.Setmeal;
import com.xb.reggie.entity.SetmealDish;
import com.xb.reggie.service.CategoryService;
import com.xb.reggie.service.DishService;
import com.xb.reggie.service.SetmealDishService;
import com.xb.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    DishService dishService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>();
        Page<SetmealDto> dtoInfo = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, dtoInfo, "record");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item,setmealDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoInfo.setRecords(list);
        return R.success(dtoInfo);
    }

    @GetMapping("{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        Long categoryId = setmeal.getCategoryId();
        String name = categoryService.getById(categoryId).getName();
        setmealDto.setCategoryName(name);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishList);

        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateByDto(setmealDto);
        return R.success("套餐修改成功");
    }

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveByDto(setmealDto);
        return R.success("套餐添加成功");
    }

    @PostMapping("/status/{st}")
    public R<String> stop(@PathVariable int st, @RequestParam("ids") List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(st);
            setmealService.updateById(setmeal);
        }
        return R.success("套餐停售");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        setmealService.deleteDtoById(ids);
        return R.success("菜品删除成功");
    }

}
