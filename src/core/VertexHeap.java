package core;

import static core.Digraph.*;

import java.util.HashMap;
import java.util.Map;

public class VertexHeap extends MinHeap<Vertex> {

    Map<Vertex, Integer> locationInHeap = new HashMap<Vertex, Integer>();
    Map<Vertex, Integer> vertexScores;

    public VertexHeap(Map<Vertex, Integer> vertexScores) {
        super();
        this.vertexScores = vertexScores;
    }

    @Override
    protected int score(Vertex vertex) {
        Integer score = vertexScores.get(vertex);
        return score == null ? Integer.MAX_VALUE : score;
    }

    @Override
	public void insert(Vertex vertex) {
        locationInHeap.put(vertex, heap.size());
		super.insert(vertex);
	}

	@Override
	public Vertex popMin() {
		Vertex min = super.popMin();
		//heapidx of removed vertex is now invalid
        locationInHeap.remove(min);
		return min;
	}

    @Override
    public void clear() {
        super.clear();
        locationInHeap.clear();
    }

	public boolean updateIfSmaller(Vertex vertex, int newScore) {
        if (!contains(vertex)) throw new IllegalArgumentException("vertex not in heap");

		// if the new score is smaller than the vertex's current score
		if(newScore < score(vertex)) {
			// update the score
            vertexScores.put(vertex, newScore);
			// perc up the updated vertex
			percUp(locationInHeap.get(vertex));
			return true;
		}
		return false;
	}

	@Override
	protected void swap(int a, int b) {
		super.swap(a, b);
        locationInHeap.put(heap.get(a), a);
        locationInHeap.put(heap.get(b), b);
	}

    public boolean contains(Vertex vertex) {
        return locationInHeap.keySet().contains(vertex);
    }

    
}
