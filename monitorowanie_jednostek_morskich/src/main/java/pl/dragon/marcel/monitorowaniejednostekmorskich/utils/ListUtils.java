package pl.dragon.marcel.monitorowaniejednostekmorskich.utils;

import java.util.List;

public final class ListUtils {
	public static <T> Object getlast(List<T> list) {
		return list == null || list.isEmpty() ? null : list.get(list.size() - 1);
	}
}
