package com.github.zzt93.syncer.common.event;


import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.Assert;

/**
 * <a href="https://dev.mysql.com/doc/internals/en/binlog-row-image.html">binlog row image format</a>
 * @author zzt
 */
public abstract class RowsEvent {

  private final Event tableMap;
  private final Map<Integer, String> indexToName;
  private List<HashMap<Integer, Object>> rows = new ArrayList<>();

  public RowsEvent(Event tableMap, Map<Integer, String> indexToName) {
    this.tableMap = tableMap;
    this.indexToName = indexToName;
  }

  public TableMapEventData getTableMap() {
    return tableMap.getData();
  }

  void addRow(HashMap<Integer, Object> row) {
    rows.add(row);
  }

  public boolean filterData(List<Integer> index) {
    Assert.isTrue(!rows.isEmpty(), "Assertion Failure: no row");
    List<HashMap<Integer, Object>> tmp = new ArrayList<>();
    for (HashMap<Integer, Object> row : rows) {
      HashMap<Integer, Object> map = new HashMap<>();
      for (Integer integer : index) {
        if (row.containsKey(integer)) {
          map.put(integer, row.get(integer));
        }
      }
      if (!map.isEmpty()) {
        tmp.add(map);
      }
    }
    rows = tmp;
    return !rows.isEmpty();
  }

  public List<HashMap<String, Object>> getRows() {
    List<HashMap<String, Object>> res = new ArrayList<>();
    for (HashMap<Integer, Object> row : rows) {
      HashMap<String, Object> map = new HashMap<>();
      for (Integer integer : row.keySet()) {
        map.put(indexToName.get(integer), row.get(integer));
      }
      res.add(map);
    }
    return res;
  }

  public abstract EventType type();
}