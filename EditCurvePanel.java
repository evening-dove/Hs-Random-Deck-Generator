
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


import java.util.*;

public class EditCurvePanel extends JPanel
{
  EditCurvePanel this_panel=this;
  
  boolean enabled;
    
  static int highest_curve=0;
  
  
  JLabel deck_size_lable=new JLabel("0 Cards", SwingConstants.CENTER);
  int deck_size_value=0;
  
  
  ArrayList all_mana_panels=new ArrayList();
  
  JPanel curve_panel=new JPanel(new FlowLayout());
  
  
  JPanel enforce_curve_panel=new JPanel(new FlowLayout());
  JRadioButton enforce_curve_yes=new JRadioButton("Yes");
  JRadioButton enforce_curve_no=new JRadioButton("No");
  JButton enforce_curve_help=new JButton("?");
  
  
  public EditCurvePanel(boolean enabled)
  {
    super(new BorderLayout());
    
    this.enabled=enabled;
    
    add(deck_size_lable, BorderLayout.NORTH);
    if (enabled==false){
      deck_size_lable.setText("30 Cards");
    }
    
    makeCurveFromDefault();
     
    updateAllPanels();
    
    add(curve_panel, BorderLayout.CENTER);
    
    
    //Sets up enforce curve options
    enforce_curve_yes.setSelected(true);
    ButtonGroup enforce_curve_group = new ButtonGroup();
    enforce_curve_group.add(enforce_curve_yes);
    enforce_curve_group.add(enforce_curve_no);
    
    enforce_curve_panel.add(new JLabel("Enforce this curve?  "));
    enforce_curve_panel.add(enforce_curve_yes);
    enforce_curve_panel.add(enforce_curve_no);
    enforce_curve_panel.add(enforce_curve_help);
    
    add(enforce_curve_panel, BorderLayout.SOUTH);
    
  }
  
  protected void updateDeckSize(int change_by){
    /*Updates the counter used to track the number of cards in the deck
    if change_by is positive, increase deck_size_value by the much
    if change_by is negative, decrease deck_size_value by the much
    */
		
    deck_size_value+=change_by;
    deck_size_lable.setText(deck_size_value+" Cards");
  }
    
  protected void updateAllPanels(){
    /*
    Used to reorient all of the cost panels after something has been
    added, removed, or changed
    */
    curve_panel.removeAll();
    
    ((ManaPanel)all_mana_panels.get(0)).cost_min=0;
    for (int i=0; i<all_mana_panels.size(); i+=1){
      ManaPanel temp_panel=(ManaPanel)all_mana_panels.get(i);
      temp_panel.index=i;
      temp_panel.updateCostLabel();
      temp_panel.updateExtraOptions();
      curve_panel.add(temp_panel);
    }
    ((ManaPanel)all_mana_panels.get(all_mana_panels.size()-1)).cost_max=((ManaPanel)all_mana_panels.get(all_mana_panels.size()-1)).cost_min;
    updateSliderMax();
    
    SwingUtilities.updateComponentTreeUI(this);
  }
  
  
  public void updateSliderMax(){
    /*
    Makes the slider used to represent card quantities at costs all chnage dinamically.
    */
      
    //determine what the highst slider value is currently at
    highest_curve=0;
    for (int i=0; i<all_mana_panels.size(); i++){
      ManaPanel temp_panel=(ManaPanel)all_mana_panels.get(i);
      
      if (highest_curve<temp_panel.card_slider.getValue()){
        highest_curve=temp_panel.card_slider.getValue();
      }
    }
    
    //if the highest amount is at 25 or higher, just set all slider max values to 30
    if (highest_curve>=25){
    
      for (int i=0; i<all_mana_panels.size(); i++){
        JSlider temp_slider=((ManaPanel)all_mana_panels.get(i)).card_slider;
        temp_slider.setMaximum(30);
        temp_slider.repaint();
      }
      return;
    }
    
  //Set all max slider values se they have the same max value
    for (int i=0; i<all_mana_panels.size(); i++){
      JSlider temp_slider=((ManaPanel)all_mana_panels.get(i)).card_slider;
      
      temp_slider.setMaximum(((int)Math.ceil((highest_curve+2)/5.0))*5);
      
      temp_slider.repaint();
      
    }
  }
    
    
  private void makeCurveFromDefault(){
    //Generates the default curve
		
    int highest_curve=0;
    
    for (int i=0; i<main_code.default_mana_curve.length; i++){
			
    //Generate the curve panel
      String temp_string=main_code.default_mana_curve[i];
      int space_index1=temp_string.indexOf(" ");
      int space_index2=temp_string.substring(space_index1+1).indexOf(" ")+space_index1+1;
      
      ManaPanel new_panel=new ManaPanel(Integer.parseInt(temp_string.substring(space_index2+1)), Integer.parseInt(temp_string.substring(0, space_index1)), Integer.parseInt(temp_string.substring(space_index1+1, space_index2)));
      
     //If this EditCurvePanel cannot be modified, get the info needed to set the sliders so they all
     //have the same max value
      if (enabled==false){
        int temp_cost=Integer.parseInt(temp_string.substring(space_index2+1));
        new_panel.amount_label.setText(""+temp_cost);
        if (temp_cost>highest_curve){
          highest_curve=temp_cost;
        }
      }
    }
    
    //If this is the EditCurvePanel that cannot be modified, set the sliders so they all
    //have the same max value
    if (enabled==false){
      for (int i=0; i<all_mana_panels.size(); i++){
        ((ManaPanel)all_mana_panels.get(i)).card_slider.setMaximum(((int)Math.ceil((highest_curve+2)/5.0))*5);
        ((ManaPanel)all_mana_panels.get(i)).updateCostLabel();
      }
    }
  }
    
  
  
  public class ManaPanel extends JPanel{
    
    private ManaPanel this_mana_panel=this;
    
    
    int index;
    
    int cost_min;
    int cost_max;
    
    boolean top_cost;
    
    JLabel cost_label=new JLabel("");
    
    JPanel amount_editing_panel=new JPanel(new BorderLayout());
    
    JSlider card_slider=new JSlider(JSlider.VERTICAL);
    boolean update_slider_state=true;
    JTextField amount_label=new JTextField("0");
    
    JPanel extra_options_panel=new JPanel(new GridLayout(0, 1));
    
    
    JPanel merge_panel=new JPanel(new BorderLayout());
    JButton merge_left_button=new JButton("<");
    JLabel merge_label=new JLabel("Merge");
    JButton merge_right_button=new JButton(">");
    
    JPanel split_panel=new JPanel(new BorderLayout());
    JButton split_center_button=new JButton("Split");
    JButton split_left_button=new JButton("<");
    JLabel split_label=new JLabel("Split");
    JButton split_right_button=new JButton(">");
    
    
    public ManaPanel(int starting_amount, int cost){
      this(starting_amount, cost, cost);
    }
  
    public ManaPanel(int starting_amount, int cost_min, int cost_max){
      super(new BorderLayout());
      
      curve_panel.add(this);
      
      this.cost_min=cost_min;
      this.cost_max=cost_max;
      
      all_mana_panels.add(this);
      
      if (this_panel.enabled==false){
        card_slider.setEnabled(false);
        amount_label.setEnabled(false);
      }
      
      card_slider.setMinimum(0);
      
      card_slider.setMajorTickSpacing(2);
      card_slider.setMinorTickSpacing(1);
      card_slider.setPaintTicks(true);
      card_slider.setPaintLabels(true);
      
      if (this_panel.enabled==true){
        card_slider.addChangeListener(new ChangeListener(){
          int previous_val=0;
          public void stateChanged(ChangeEvent event){
            if (update_slider_state==false){
              return;
            }
            JSlider source=(JSlider)event.getSource();
            
            
            this_mana_panel.amount_label.setText(""+source.getValue());
            
            updateDeckSize(source.getValue()-previous_val);
            previous_val=source.getValue();
            
            previous_val=source.getValue();
            
            updateSliderMax();
            
          }
        }
        );
        
        
        amount_label.addKeyListener(new KeyListener(){
          JTextField source;
          String source_text;
          int previous_val;
          public void keyPressed(KeyEvent event) {
          }
        
          public void keyReleased(KeyEvent event) {
            source=(JTextField)event.getSource();
            source_text=source.getText();
            
            if (source_text.length()==0){
              updateDeckSize(-previous_val);
              this_mana_panel.card_slider.setValue(0);
              source.setText("");
              return;
            }
            if ("0123456789".indexOf(source_text.charAt(source_text.length()-1))==-1){
              source.setText(source_text.substring(0, source_text.length()-1));
              return;
            }
            
            int source_val=Integer.parseInt(source_text);
            if (source_val>30){
              source_val=30;
            }
            
            if (source_val>card_slider.getMaximum()){
              update_slider_state=false;
              card_slider.setMaximum(30);
              update_slider_state=true;
            }
            this_mana_panel.card_slider.setValue(source_val);
          }
          
          public void keyTyped(KeyEvent event){
          }
        }
        );
      }
      
      //keep track of what the highest value is
      card_slider.setValue(starting_amount);
      if (highest_curve<starting_amount){
        highest_curve=starting_amount;
      }
      
      amount_editing_panel.add(card_slider, BorderLayout.CENTER);
      amount_editing_panel.add(amount_label, BorderLayout.SOUTH);
      
      
      if (this_panel.enabled==true){
        merge_left_button.addActionListener(new MergeButtonListener());
        merge_right_button.addActionListener(new MergeButtonListener());
        
        split_center_button.addActionListener(new SplitButtonListener());
        split_left_button.addActionListener(new SplitButtonListener());
        split_right_button.addActionListener(new SplitButtonListener());
      }
      
      
      
      
      add(cost_label, BorderLayout.NORTH);
      add(amount_editing_panel, BorderLayout.CENTER);
      if (this_panel.enabled==true){
        add(extra_options_panel, BorderLayout.SOUTH);
      }
      
      
    }
    
    
    private void updateCostLabel(){
      //Updates the label that shows how many cards will be put in each cost.
        
      //If this is the max cost
      if (index==all_mana_panels.size()-1){
        cost_label.setText(""+cost_min+"+ Mana");
      }
			
      //if this is a single cost
      else if (cost_min==cost_max){
        cost_label.setText(""+cost_min+" Mana");
      }
			
      //if this is multiple costs
      else{
         cost_label.setText(""+cost_min+"-"+cost_max+" Mana");
       }
    }
      
    protected void updateExtraOptions(){
      
      
      extra_options_panel.removeAll();
      merge_panel.removeAll();
      
      if (all_mana_panels.size()!=1){
        if (index!=0){
          merge_panel.add(merge_left_button, BorderLayout.WEST);
        }
        merge_panel.add(merge_label, BorderLayout.CENTER);
        if (index!=all_mana_panels.size()-1){
          merge_panel.add(merge_right_button,BorderLayout.EAST);
        }
        extra_options_panel.add(merge_panel);
        
      }
      
      split_panel.removeAll();
      if ((cost_min!=cost_max || index==all_mana_panels.size()-1) && cost_min!=10){
        if (Math.abs(cost_min-cost_max)!=1 && all_mana_panels.size()!=1){
          if (index!=0){
            split_panel.add(split_left_button, BorderLayout.WEST);
          }
          split_panel.add(split_label, BorderLayout.CENTER);
          if (index!=all_mana_panels.size()-1){
            split_panel.add(split_right_button, BorderLayout.EAST);
          }
        }else{
          split_panel.add(split_center_button, BorderLayout.CENTER);
        }
        extra_options_panel.add(split_panel);
      }     
      
      for (int i=extra_options_panel.getComponents().length; i<2; i+=1){
        extra_options_panel.add(new JLabel(""));
        
        
      }
      
      
      updateCostLabel();
      
    }
    
     
    
    
    
    
    
    protected class MergeButtonListener implements ActionListener{
			
      public void actionPerformed(ActionEvent event){
        JButton source=(JButton)event.getSource();
        ManaPanel merge_with;
        if (source.getText()=="<"){
          //Merge with a lower cost
          merge_with=((ManaPanel)all_mana_panels.get(index-1));
          merge_with.cost_max=cost_max;
        }else{
          //Merge with a higher cost
          merge_with=((ManaPanel)all_mana_panels.get(index+1));
          merge_with.cost_min=cost_min;
        }
        int new_amount=card_slider.getValue()+merge_with.card_slider.getValue();
        
        updateDeckSize(-card_slider.getValue());
          
        merge_with.amount_label.setText(""+new_amount);
        merge_with.card_slider.setValue(new_amount);
        
        //If merging with the max cost, this makes it easyer for the code
        //to be able to detect that fact.
        if (merge_with.index==all_mana_panels.size()-1){
          merge_with.cost_max=merge_with.cost_min;
        }
        
        all_mana_panels.remove(this_mana_panel);
        
        updateAllPanels();
      }
      
    }
    
    
    
    protected class SplitButtonListener implements ActionListener{
      //Split one post panel away from a merged costs
      public void actionPerformed(ActionEvent event){
        JButton source=(JButton)event.getSource();
        ManaPanel new_panel;
        int newPanelCardsAmount=(int)(card_slider.getValue()/((cost_max-cost_min)+1));
				
        if (source.getText()==">"){
          //Split the highest of the merged costs
          if (all_mana_panels.size()==1){
            newPanelCardsAmount = (int)(card_slider.getValue()/10);
            new_panel=new ManaPanel(newPanelCardsAmount, cost_max+1);
            card_slider.setValue(card_slider.getValue()-newPanelCardsAmount);
            all_mana_panels.add(index+1, new_panel);
          }else{
            new_panel=new ManaPanel(newPanelCardsAmount, cost_max);
            cost_max-=1;
            all_mana_panels.add(index+1, new_panel);
          }
        }else{
          //Split the lowest of the merged costs, (or if there
          //are only 2 costs merged)
          new_panel=new ManaPanel(newPanelCardsAmount, cost_min);
          cost_min+=1;
          all_mana_panels.add(index, new_panel);
        }
        card_slider.setValue(card_slider.getValue()-newPanelCardsAmount);
        all_mana_panels.remove(all_mana_panels.size()-1);
        
        
        updateAllPanels();
      }
      
    }
    
  }
  
  
  
  
  public void transferCurve(){
    /*
    Get information on the selected curve so it can be given to the
    text file.
    */
		
    for (int i=0;i<all_mana_panels.size(); i+=1){
      ManaPanel temp_mana=(ManaPanel)(all_mana_panels.get(i));
      main_code.output_deck_text.add(temp_mana.cost_min+" "+temp_mana.cost_max+" "+temp_mana.card_slider.getValue());
    }
    main_code.output_deck_text.add(enforce_curve_yes.isSelected());
  }
}


