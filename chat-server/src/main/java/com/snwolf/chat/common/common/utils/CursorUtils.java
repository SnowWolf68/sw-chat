package com.snwolf.chat.common.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.snwolf.chat.common.common.domain.vo.req.CursorPageBaseReq;
import com.snwolf.chat.common.common.domain.vo.resp.CursorPageBaseResp;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/4/2024
 * @Description: 游标翻页工具类
 */
public class CursorUtils {


    /**
     *
     * @param cursorPageBaseReq: 游标查询参数
     * @param initWrapper:额外的查询条件, 这里为了方便, 将wrapper使用Consumer接口进行一层封装, 调用的时候可以直接使用lambda表达式传入
     * @param mapper: mp执行分页查询的mapper/service对象
     * @param cursorColumn: 重点!!! 光标所在字段/对应实体类的属性: 在使用page构建游标的查询条件时,
     *              需要使用类似User::id的方式指定字段, 因此这里我们也按照类似的方式进行传入, 需要使用SFunction<T, ?>进行接收
     * @Return: 游标分页查询结果
     * @param <T> 对应实体类类型
     *
     * @note
     *   关键: 由于cursorPageReq中的cursor字段是String类型, 而我们需要查询的游标字段的类型是通过SFunction<T, ?>进行指定的
     *        所以我们需要通过SFunction<T, ?>找到游标对应的类型, 并将String类型的游标cursor转换成对应类型,
     *        然后在lt(T::getXxx, ?)中进行传入
     *   这里由于T::getXxx类似的参数在mp中经常用到, 因此mp中一定有解析这种参数的方法, 因此我们采用类似mp中的方法进行解析
     */
    public static <T> CursorPageBaseResp<T> cursorPageQuery(CursorPageBaseReq cursorPageBaseReq,
                                                            Consumer<LambdaQueryWrapper<T>> initWrapper,
                                                            IService<T> mapper,
                                                            SFunction<T, ?> cursorColumn) throws NoSuchFieldException {
        // 得到游标cursor对应的具体类型
        Class<?> cursorType = getReturnType(cursorColumn);
        // 添加额外条件
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        initWrapper.accept(wrapper);
        // 构建游标cursor相关条件
        if(StrUtil.isNotBlank(cursorPageBaseReq.getCursor())){
            // 将cursor游标转换成其对应的具体类型
            Object cursor = parseCursor(cursorType, cursorPageBaseReq.getCursor());
            wrapper.lt(cursorColumn, cursor);
        }
        // 游标移动方向(结果的升降序)
        wrapper.orderByDesc(cursorColumn);
        // 执行查询
        Page<T> pageResult = mapper.page(new Page<>(0, cursorPageBaseReq.getPageSize(), false), wrapper);
        // 获取isLast以及新的cursor
        String cursorStr = Optional.ofNullable(pageResult.getRecords())
                .map(records -> CollectionUtil.getLast(records))
                .map(cursorColumn)
                .map(cursor -> cursorToString(cursor))
                .orElse(null);
        Boolean isLast = pageResult.getRecords().size() != cursorPageBaseReq.getPageSize();
        return new CursorPageBaseResp<>(cursorStr, isLast, pageResult.getRecords());
    }

    /**
     * 根据cursor的具体类型cursorType, 将Str类型的cursorStr转换成对应类型的对象
     * @param cursorType: cursor的具体类型
     * @param cursorStr: String类型的cursor对象
     * @Return: 转换成具体类型的cursor对象
     */
    private static Object parseCursor(Class<?> cursorType, String cursorStr) {
        if (LocalDateTime.class.isAssignableFrom(cursorType)) {
            return new Date(Long.parseLong(cursorStr));
        } else {
            return cursorStr;
        }
    }

    /**
     * 将具体类型的cursor对象转换成String类型
     * @param: 具体类型的cursor
     * @Return: String类型的cursor
     * @note: 对于时间类型的cursor, 这里我们选择将其转化成毫秒值
     */
    private static String cursorToString(Object cursor) {
        if (cursor instanceof Date) {
            return String.valueOf(((Date) cursor).getTime());
        } else {
            return cursor.toString();
        }
    }


    /**
     * 通过SFunction<T, ?>获取返回字段的类型
     * @param func: 对应SFunction参数, 例如: User::getId
     * @return: 返回的字段类型
     * @param <T>
     * @throws NoSuchFieldException
     */
    private static <T> Class<?> getReturnType(SFunction<T, ?> func) throws NoSuchFieldException {
        // 通过mp中的方法, 得到cursorColumn返回的字段所在的类
        com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda lambda = com.baomidou.mybatisplus.core.toolkit.LambdaUtils.resolve(func);
        // aClass就是cursorColumn返回的字段所在的类
        Class<?> aClass = lambda.getInstantiatedType();
        // 得到cursorColumn返回的字段名
        String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        // 通过反射从类(aClass)中拿到cursorColumn返回的字段名对应的字段类型
        Field field = aClass.getDeclaredField(fieldName);
        // 暴力反射, 解除private的限制
        field.setAccessible(true);
        return field.getType();
    }
}
