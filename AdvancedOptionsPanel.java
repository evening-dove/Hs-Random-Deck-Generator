
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.*;


public class AdvancedOptionsPanel extends JPanel
{
  
  AdvancedOptionsPanel this_panel=this;
  
  //build the UI for the warning
  JPanel warning_panel=new JPanel(new BorderLayout());
  JLabel warning1_label=new JLabel("Warning!", SwingConstants.CENTER);
  JLabel warning2_label=new JLabel("<html>Depending on the size of your collection,<br>options on this page may render your<br>decks impossible to build.<br><br></html>", SwingConstants.CENTER);
  
  //These panels are used so that the UI is nicely spaced
  JPanel options_panel_1=new JPanel(new BorderLayout());
  JPanel options_panel_2=new JPanel(new BorderLayout());
  JPanel options_panel_3=new JPanel(new BorderLayout());
  JPanel options_panel_4=new JPanel(new BorderLayout());
  JPanel options_panel_5=new JPanel(new BorderLayout());
  
  //Options and UI for what type of curve to use
  JPanel curve_type_panel=new JPanel(new GridLayout(1,0));
  JRadioButton disable_curve=new JRadioButton("Disable curve");
  JRadioButton default_curve=new JRadioButton("Use default curve");
  JRadioButton custom_curve=new JRadioButton("Use custom curve");
  
  EditCurvePanel custom_curve_panel=new EditCurvePanel(true);
  EditCurvePanel default_curve_panel=new EditCurvePanel(false);
  
  //Options and UI for what type of modifiers to use
  JPanel modifiers_panel=new JPanel(new GridLayout(1,0));
  JRadioButton disable_modifiers=new JRadioButton("Disable modifiers");
  JRadioButton default_modifiers=new JRadioButton("Use default modifiers");
  JRadioButton custom_modifiers=new JRadioButton("Use custom modifiers");
  
  EditModifiersPanel custom_modifiers_panel=new EditModifiersPanel(true);
  EditModifiersPanel default_modifiers_panel=new EditModifiersPanel(false);
  
  //Option for the minimum number of legendary cards a deck
  JPanel legendary_amount_panel=new JPanel(new FlowLayout());
  JLabel legendary_amount_text=new JLabel("Set the minimum number of legandary minions per deck:");
  JTextField legendary_amount_field=new JTextField("0");
  
  //Option to enable the legendary rules feature
  JPanel legendary_rules_panel=new JPanel(new FlowLayout());
  JRadioButton legendary_rules_yes=new JRadioButton("Yes");
  JRadioButton legendary_rules_no=new JRadioButton("No");
  JButton legendary_rules_help=new JButton("?");
  
  //Option to enable simultanious deckbuilding
  JPanel simultanious_panel=new JPanel(new FlowLayout());
  JRadioButton simultanious_yes=new JRadioButton("Yes");
  JRadioButton simultanious_no=new JRadioButton("No");
  JButton simultanious_help=new JButton("?");
  
  
  JButton back_button=new JButton("Back");
  
  
  
  public AdvancedOptionsPanel()
  {
    super(new BorderLayout());
    
    
    //Adds Warning to top
    warning_panel.add(warning1_label, BorderLayout.NORTH);
    warning_panel.add(warning2_label, BorderLayout.CENTER);
    add(warning_panel, BorderLayout.NORTH);
    
    //Sets up Curve options
    disable_curve.setSelected(true);
    ButtonGroup curve_group = new ButtonGroup();
    curve_group.add(disable_curve);
    curve_group.add(default_curve);
    curve_group.add(custom_curve);
    
    disable_curve.addActionListener(new optionListener());
    curve_type_panel.add(disable_curve);
    default_curve.addActionListener(new optionListener());
    curve_type_panel.add(default_curve);
    custom_curve.addActionListener(new optionListener());
    curve_type_panel.add(custom_curve);
    
    
    //Sets up Modifier options
    disable_modifiers.setSelected(true);
    ButtonGroup modifiers_group = new ButtonGroup();
    modifiers_group.add(disable_modifiers);
    modifiers_group.add(default_modifiers);
    modifiers_group.add(custom_modifiers);
    
    disable_modifiers.addActionListener(new optionListener());
    modifiers_panel.add(disable_modifiers);
    default_modifiers.addActionListener(new optionListener());
    modifiers_panel.add(default_modifiers);
    custom_modifiers.addActionListener(new optionListener());
    modifiers_panel.add(custom_modifiers);
    
    //Sets up how many legendary cards will be used at minimum
    legendary_amount_field.setPreferredSize(new Dimension(30, 25));
    
    legendary_amount_panel.add(legendary_amount_text);
    legendary_amount_panel.add(legendary_amount_field);
    
    //Sets up legendary optimization options
    legendary_rules_yes.setSelected(true);
    ButtonGroup legendary_rules_group = new ButtonGroup();
    legendary_rules_group.add(legendary_rules_yes);
    legendary_rules_group.add(legendary_rules_no);
    
    legendary_rules_panel.add(new JLabel("Modify Decks to Semi-Optimise Legendary Cards?  "));
    legendary_rules_panel.add(legendary_rules_yes);
    legendary_rules_panel.add(legendary_rules_no);
    legendary_rules_panel.add(legendary_rules_help);
    
    
    //Sets up simultanious options
    simultanious_no.setSelected(true);
    ButtonGroup simultanious_group = new ButtonGroup();
    simultanious_group.add(simultanious_yes);
    simultanious_group.add(simultanious_no);
    
    simultanious_panel.add(new JLabel("Enable Deckbuilding restrictions across ALL decks?  "));
    simultanious_panel.add(simultanious_yes);
    simultanious_panel.add(simultanious_no);
    simultanious_panel.add(simultanious_help);
    
    
    //Puts the UI together 
    add(options_panel_1, BorderLayout.CENTER);
    rebuild_options();
    
    options_panel_3.add(legendary_amount_panel, BorderLayout.CENTER);
    options_panel_3.add(options_panel_4, BorderLayout.SOUTH);
    options_panel_4.add(legendary_rules_panel, BorderLayout.CENTER);
    options_panel_4.add(options_panel_5, BorderLayout.SOUTH);
    options_panel_5.add(simultanious_panel, BorderLayout.CENTER);
    
    
    
    
    //Button to return from this menu
    back_button.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
        if (custom_curve.isSelected()==true && custom_curve_panel.deck_size_value!=30){
          int confirm_back=JOptionPane.showConfirmDialog(this_panel, "Warning!\nYou are trying to make a deck with an illegal number of cards.\nThis deck is not a valid Hearthstone deck.\n\nAre you sure?");
          if (confirm_back!=JOptionPane.YES_OPTION){
            return;
          }
        }
        main_code.main_panel.removeAll();
        
        
        main_code.main_panel.add(main_code.deck_options_panel);
        SwingUtilities.updateComponentTreeUI(main_code.main_panel);
      }
    }
    );
    
    
    add(back_button, BorderLayout.SOUTH);
    
    
  }
  
  
  public void rebuild_options(){
    
    
    
    options_panel_1.removeAll();
    options_panel_1.add(curve_type_panel, BorderLayout.NORTH);
    
    if (default_curve.isSelected()==true){
      options_panel_1.add(default_curve_panel, BorderLayout.CENTER);
    }
    if (custom_curve.isSelected()==true){
      options_panel_1.add(custom_curve_panel, BorderLayout.CENTER);
    }
    
    options_panel_1.add(options_panel_2, BorderLayout.SOUTH);
    
    
    options_panel_2.removeAll();
    options_panel_2.add(modifiers_panel, BorderLayout.NORTH);
    
    if (custom_modifiers.isSelected()==true){
      options_panel_2.add(custom_modifiers_panel, BorderLayout.CENTER);
    }
    if (default_modifiers.isSelected()==true){
      options_panel_2.add(default_modifiers_panel, BorderLayout.CENTER);
    }
    
    options_panel_2.add(options_panel_3, BorderLayout.SOUTH);
    
    
    
    revalidate();
    repaint();
  }
  
  
  
  private class optionListener implements ActionListener{
    public void actionPerformed(ActionEvent event){
      rebuild_options();
    }
  }
  
  
  
  
  public void transferAdvanced(){
  /*
  Get information on what options were enabled so it can be given to the
  text file.
  */
    
    
    if (disable_curve.isSelected()==true){
    //this will make it so no curve is created
      main_code.output_deck_text.add("0 10 30");
      main_code.output_deck_text.add("true");
    }
    if (default_curve.isSelected()==true){
    //Use the values from the default curve
      for (int i=0; i<main_code.default_mana_curve.length; i++){
        main_code.output_deck_text.add(main_code.default_mana_curve[i]);
      }
      main_code.output_deck_text.add("true");
    }
    if (custom_curve.isSelected()==true){
    //use the curve that was custom made by the user
      custom_curve_panel.transferCurve();
    }
    main_code.output_deck_text.add("");
    
    if (disable_modifiers.isSelected()==true){
    //setting these values to 100 makes it so the cards
    //recieve no bonus
      main_code.output_deck_text.add("100");
      main_code.output_deck_text.add("100");
      main_code.output_deck_text.add("100");
      main_code.output_deck_text.add("100");
    }
    if (default_modifiers.isSelected()==true){
    //Use the default modifier values
      default_modifiers_panel.transferModifiers();
    }
    if (custom_modifiers.isSelected()==true){
    //If the user chose to use tehir own custom modifiers
      custom_modifiers_panel.transferModifiers();
    }
    main_code.output_deck_text.add("");
    
    main_code.output_deck_text.add(legendary_amount_field.getText()); //The minimum amount of legendary cards
    main_code.output_deck_text.add("");
    main_code.output_deck_text.add(legendary_rules_yes.isSelected()); //If legendary optimization should happen
    main_code.output_deck_text.add("");
    main_code.output_deck_text.add(simultanious_yes.isSelected()); //if simultanious deckbuilding should happen
    
  }
}