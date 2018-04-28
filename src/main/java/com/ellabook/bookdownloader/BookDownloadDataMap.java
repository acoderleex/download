package com.ellabook.bookdownloader;

import java.util.HashMap;

/**
 * 帮助下载类展示数据和状态
 * Created by java on 2018/2/2.
 */

 class BookDownloadDataMap {
    private static BookDownloadDataMap INSTANCE;
    private HashMap<String, BookDownloadData> hashMap = new HashMap<>();

    public static BookDownloadDataMap getInstance() {
        if (INSTANCE == null){
            INSTANCE = new BookDownloadDataMap();
        }
        return INSTANCE;
    }

    public static void clear(){
        if (INSTANCE != null){
            INSTANCE.hashMap.clear();
        }
        INSTANCE = null;
    }

    public void addData(BookDownloadData data){
        hashMap.put(data.getBookcode(),data);
    }

    public BookDownloadData getData(String bookcode){
        BookDownloadData data = null;
        if (hashMap.containsKey(bookcode)){
            data = hashMap.get(bookcode);
        }
        return data;
    }

    public void removeData(String bookcode){
        hashMap.remove(bookcode);
        DownloadLogcat.d("show","removeData "+bookcode);
    }

    public HashMap<String, BookDownloadData> getHashMap() {
        return hashMap;
    }

}
