package com.xb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xb.reggie.common.R;
import com.xb.reggie.dto.DishDto;
import com.xb.reggie.entity.Category;
import com.xb.reggie.entity.Dish;
import com.xb.reggie.service.CategoryService;
import com.xb.reggie.service.DishFlavorService;
import com.xb.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    DishService dishService;

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        if(dishDto != null) {
            dishService.saveDto(dishDto);
            return R.success("菜品添加成功");
        }
        return R.error("菜品添加失败");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("菜品分页");
        Page<Dish> pageInfo = new Page(page, pageSize);
        Page<DishDto> dtoInfo = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getSort);
        dishService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoInfo, "record");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dtoInfo.setRecords(list);
        return R.success(dtoInfo);
    }

    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = dishService.getDtoById(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("修改菜品");
        dishService.updateDto(dishDto);
        return R.success("修改菜品成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        dishService.deleteDtoById(ids);
        return R.success("菜品删除成功");
    }

    @PostMapping("/status/{st}")
    public R<String> stop(@PathVariable int st, @RequestParam("ids") List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(st);
            dishService.updateById(dish);
        }
        return R.success("菜品停售");
    }

    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        log.info("套餐选择菜品");
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);
        return R.success(dishList);
    }
}
