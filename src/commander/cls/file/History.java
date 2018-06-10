/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.file;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 *
 * @author Nam
 */
public class History {
    private ArrayList<Path> history;
    private int pointer = -1;
    
    public History() {
        history = new ArrayList<>();
    }
    
    public void clear() {
        history.clear();
        pointer = -1;
    }
    
    public void add(Path path) { 
        if ((!history.isEmpty() && history.get(pointer).compareTo(path) != 0) || history.isEmpty())  {
            removeRange(pointer + 1, history.size() - 1);
            history.add(path);
            pointer = pointer + 1;
        }
    }
    
    public Path backward() {
        if (!history.isEmpty() && pointer - 1 >= 0) {
            pointer = pointer - 1;
            return history.get(pointer);
        }
        return null;
    }
    
    public Path forward() {
        if (!history.isEmpty() && pointer + 1 < history.size()) {
            pointer = pointer + 1;
            return history.get(pointer);
        }
        return null;
    }
    
    protected void removeRange(int fromIndex, int toIndex) {
        for (int i = toIndex; i >= fromIndex; i--) {
            history.remove(i);
        }
    }
    
}
