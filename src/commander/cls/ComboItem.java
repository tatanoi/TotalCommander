/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls;

/**
 *
 * @author Nam
 */
public class ComboItem {
    private String key;
    private RootInfo value;

    public ComboItem(String key, RootInfo value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return key;
    }

    public String getKey()
    {
        return key;
    }

    public RootInfo getValue()
    {
        return value;
    }
}
