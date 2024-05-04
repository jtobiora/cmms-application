package ng.upperlink.nibss.cmms.util.emandate.soap.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Nduka on 25/09/2018.
 * Common collection operations
 */
public class CollectionUtils {

    public static <T> List<T> intersect(Collection<T> listA, Collection<T> listB) {

        List<T> newList = new ArrayList<>();
        for (T item : listA) {
            if (listB.contains(item)) {
                newList.add(item);
            }
        }
        return newList;
    }

    public static <T> List<T> difference(Collection<T> listA, Collection<T> listB) {

        List<T> newList = new ArrayList<>();
        for (T item : listB) {
            if (!listA.contains(item)) {
                newList.add(item);
            }
        }
        return newList;
    }

    public static <T> Collection<T> union(Collection<T> listA, Collection<T> listB) {

        for (T item : listB) {
            if (!listA.contains(item)) {
                listA.add(item);
            }
        }
        return listA;
    }
    public static <V> Map<V, Integer> flip(Collection<V> list) {
        Map<V, Integer> newMap = new LinkedHashMap<>();
        Integer i = 0;
        for (V item : list) {
            newMap.put(item, i);
            i++;
        }
        return newMap;
    }

    public static <T> Map<String, T> keyBy(Collection<T> list, String keyField) {

        Map<String, T> newMap = new LinkedHashMap<>();
        for (T item : list) {
            Class<?> c = item.getClass();
            try {
                Method k = c.getMethod("get"+CaseConverter.capitalize(keyField));
                newMap.put(k.invoke(item).toString(), item);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                newMap.put(item.toString(), item);
            }
        }

        return newMap;
    }

    public static <T> Map<String, Object> keyBy(Collection<T> list, String keyField, String valueField) {

        Map<String, Object> newMap = new LinkedHashMap<>();
        for (T item : list) {
            Class<?> c = item.getClass();
            try {
                Method k = c.getMethod("get"+CaseConverter.capitalize(keyField));
                String[] valueFields = valueField.split("[.]");
                Method v;
                Object val;
                if(valueFields.length > 1){
                    Method vChild = c.getMethod("get"+CaseConverter.capitalize(valueFields[0]));
                    Object itemChild = vChild.invoke(item);
                    Class<?> cChild= itemChild.getClass();
                    v = cChild.getMethod("get"+CaseConverter.capitalize(valueFields[1]));
                    val = v.invoke(itemChild);
                } else{
                    v = c.getMethod("get"+CaseConverter.capitalize(valueField));
                    val = v.invoke(item);
                }
                newMap.put(k.invoke(item).toString(), val);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                newMap.put(item.toString(), item);
            }
        }

        return newMap;
    }


    public static <T> boolean isUnique(List<T> list) {
        Set<T> set = new HashSet<>(list);
        return set.size() == list.size();
    }
}
