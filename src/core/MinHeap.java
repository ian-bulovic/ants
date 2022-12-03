package core;

import java.util.ArrayList;

public abstract class MinHeap<T> {
	protected ArrayList<T> heap;

	protected abstract int score(T t);

	public MinHeap() {
		heap = new ArrayList<T>();
	}

	public void insert(T t) {
		heap.add(t);
		percUp(heap.size()-1);
	}

	public T popMin() {
		if(heap.isEmpty()) throw new Error("cannot remove from empty heap");

		// swap min with last vertex in heap
		T min = heap.get(0);
		swap(0, heap.size()-1);

		// remove the last element
		heap.remove(heap.size()-1);

		// perc down the new root
		if(!isEmpty()) percDown(0);

		return min;
	}

	protected void percDown(int idx) {
		if(idx < 0 || idx >= heap.size()) throw new IllegalArgumentException("invalid index");
		if(isLeaf(idx)) return;
        int r = right(idx);
        int l = left(idx);
		if(score(heap.get(idx)) > score(heap.get(l))) {
			if(score(heap.get(r)) < score(heap.get(l))) {
				swap(idx, r);
				percDown(r);
			} else {
				swap(idx, l);
				percDown(l);
			}
		} else if(score(heap.get(idx)) > score(heap.get(r))) {
			swap(idx, r);
			percDown(r);
		}
	}

	protected void percUp(int idx) {
		if(idx < 0 || idx >= heap.size()) throw new IllegalArgumentException("invalid index");

		if(idx > 0 && score(heap.get(idx)) < score(heap.get(parent(idx)))) {
			swap(idx, parent(idx));
			percUp(parent(idx));
		}
	}

	protected int left(int idx)   { return (idx * 2) + 1; }
	protected int right(int idx)  { return (idx * 2) + 2; }
	protected int parent(int idx) { return (idx - 1) / 2; }

	protected boolean isLeaf(int idx) {
		return right(idx) >= heap.size();
	}

	protected void swap(int a, int b) {
		T tmp = heap.get(a);
		heap.set(a, heap.get(b));
		heap.set(b, tmp);
	}

	public int size() {
		return heap.size();
	}

	public boolean isEmpty() {
		return heap.isEmpty();
	}

	public void clear() {
		heap.clear();
	}
}
