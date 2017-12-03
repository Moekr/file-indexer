package com.moekr.indexer.util;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class ToolKit {
    public static LocalDateTime convertTimeStamp(long timeStamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault());
    }

    public static String convertUri(String uri){
        return uri.replace('/', File.separatorChar);
    }

    public static String convertPath(String path){
        return path.replace(File.separatorChar, '/');
    }

    public static String convertSize(long size){
        if(size < 0){
            return "0B";
        }else if(size < 1024){
            return size + "B";
        }else if(size < 1024 * 1024){
            return (int)(size / 1024.0 * 100) / 100.0 + "KB";
        }else if(size < 1024 * 1024 * 1024){
            return (int)(size/(1024.0 * 1024.0) * 100) / 100.0 + "MB";
        }else{
            return (int)(size/(1024.0 * 1024.0 * 1024.0) * 100) / 100.0 + "GB";
        }
    }

    public static String convertDate(LocalDateTime localDateTime){
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + " " + localDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public static <T> List<T> iterableToList(Iterable<T> iterable){
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    public static <T> List<T> sort(List<T> list, Comparator<T> comparator){
        list.sort(comparator);
        return list;
    }

    public static void assertNotNull(Object entity){
        if(entity == null){
            throw new NotFoundException();
        }
    }
}
