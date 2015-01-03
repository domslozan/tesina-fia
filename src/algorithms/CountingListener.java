package algorithms;

import java.util.HashMap;
import java.util.Map;

public class CountingListener<V, E> implements PathfinderEventListener<V, E> {

    private static class Count {

        public int opened, closed;
    }
    private Map<Pathfinder<V, E>, Count> count;

    public CountingListener() {
        count = new HashMap<Pathfinder<V, E>, Count>();
    }

    @Override
    public void openVertex(Pathfinder<V, E> source, V v) {
        Count c = count.get(source);
        if (c != null) {
            c.opened++;
        } else {
            c = new Count();
            c.opened = 1;
            c.closed = 0;
        }
        count.put(source, c);
    }

    @Override
    public void closeVertex(Pathfinder<V, E> source, V v) {
        Count c = count.get(source);
        if (c != null) {
            c.closed++;
        } else {
            c = new Count();
            c.opened = 0;
            c.closed = 1;
        }
        count.put(source, c);
    }

    public int openedCount(Pathfinder<V, E> source) {
        Count c = count.get(source);
        if (c == null) {
            return 0;
        }
        return c.opened;
    }

    public int closedCount(Pathfinder<V, E> source) {
        Count c = count.get(source);
        if (c == null) {
            return 0;
        }
        return c.closed;
    }

    public int nowOpenCount(Pathfinder<V, E> source) {
        Count c = count.get(source);
        if (c == null) {
            return 0;
        }
        return c.opened - c.closed;
    }

    public void clear() {
        count.clear();
    }
}
