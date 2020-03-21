/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;
import java.time.LocalDate;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;

/**
 *
 * @author becrevex
 */
public class AnchorPaneNode extends AnchorPane{
    private LocalDate date;
    public AnchorPaneNode(Node... children) {
        super(children);
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public LocalDate getDate() {
        return date;
    }
}
