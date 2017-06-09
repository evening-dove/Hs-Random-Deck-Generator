
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

import java.util.*;

public class PickSetsPanel extends JPanel
{
  private PickSetsPanel this_panel=this;
  ArrayList all_sets=new ArrayList();
  
  JPanel set_boxes_panel=new JPanel(new GridLayout(0, 3, 1, 5));
  
  JCheckBox use_standard_box=new JCheckBox("Use Standard Sets");
  
  public PickSetsPanel()
  {
    super(new BorderLayout());
    
		//Set up UI
    add(new JLabel("Please Select what Sets to use.", SwingConstants.CENTER), BorderLayout.NORTH);
    add(set_boxes_panel, BorderLayout.CENTER);
    add(use_standard_box, BorderLayout.SOUTH);
    
		//Standard sets
    new ExpCheckBox("Classic", true);
    new ExpCheckBox("Journey to Un'Goro", true);
    new ExpCheckBox("Mean Streets of Gadgetzan", true);
    new ExpCheckBox("One Night In Karazhan", true);
    new ExpCheckBox("Whispers of the Old Gods", true);
    
		//Wild sets
    new ExpCheckBox("League of Explorers", false);
    new ExpCheckBox("The Grand Tournament", false);
    new ExpCheckBox("Blackrock Mountain", false);
    new ExpCheckBox("Goblins Vs Gnomes", false);
    new ExpCheckBox("Naxxramas", false);
    new ExpCheckBox("Hall of Fame", false);
    
    
    //Checkbox for picking only the standard sets
    use_standard_box.addItemListener(new ItemListener(){
      public void itemStateChanged(ItemEvent event){
        
        if (event.getStateChange()==ItemEvent.DESELECTED){
          
          for (int i=0; i<all_sets.size(); i++){
            ((ExpCheckBox)all_sets.get(i)).setEnabled(true);
          }
          
        }else{
					//Find all standard sets and make them checked
          for (int i=0; i<all_sets.size(); i++){
            ExpCheckBox temp_set=((ExpCheckBox)all_sets.get(i));
            temp_set.setEnabled(false);
            
            if (temp_set.is_standard==true){
              temp_set.setSelected(true);
            }else{
              temp_set.setSelected(false);
            }
          }
        }
      }
    });
    
  }
  
  
  protected class ExpCheckBox extends JCheckBox
  {
		//A checkbox that indictaes if a certain set should be used
		
    String name;
    boolean is_standard;
    
    public ExpCheckBox(String name, boolean is_standard)
    {
      super(name);
      this.name=name;
      this.is_standard=is_standard;
      
      set_boxes_panel.add(this);
      all_sets.add(this);
      
    }
  }
	
  public void transferSets(){
		/*
		Get information on the selected curve so it can be given to the
		text file.
    */
		
    for (int i=0;i<all_sets.size(); i+=1){
      ExpCheckBox temp_box=(ExpCheckBox)all_sets.get(i);
      if (temp_box.isSelected()==true){
        main_code.output_deck_text.add(temp_box.name);
      }
    }
  }
}