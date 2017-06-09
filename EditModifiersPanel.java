
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


import java.util.*;



public class EditModifiersPanel extends JPanel
{
  JPanel spell_chance_panel=new JPanel(new FlowLayout());
  JTextField spell_chance_field=new JTextField(""+main_code.default_spell_chance);
  
  JPanel weapon_chance_panel=new JPanel(new FlowLayout());
  JTextField weapon_chance_field=new JTextField(""+main_code.default_weapon_chance);
  
  JPanel hero_chance_panel=new JPanel(new FlowLayout());
  JTextField hero_chance_field=new JTextField(""+main_code.default_hero_chance);
  
  JPanel legendary_chance_panel=new JPanel(new FlowLayout());
  JTextField legendary_chance_field=new JTextField(""+main_code.default_legendary_chance);
  
  
  
  
  
  public EditModifiersPanel(boolean enabled)
  {
    super(new GridLayout(0,1));
    
    add(new JLabel("Note: Entering a value below 100% makes the card less likely to appear"));
    
    //Set up the UI for Spell Modifiers
    spell_chance_panel.add(new JLabel("Spells "));
    spell_chance_field.setPreferredSize(new Dimension(30, 25));
    spell_chance_panel.add(spell_chance_field);
    spell_chance_panel.add(new JLabel("% boost."));
    add(spell_chance_panel);
    
    //Set up the UI for weapon Modifiers
    weapon_chance_panel.add(new JLabel("Weapons recieve a "));
    weapon_chance_field.setPreferredSize(new Dimension(30, 25));
    weapon_chance_panel.add(weapon_chance_field);
    weapon_chance_panel.add(new JLabel("% boost."));
    add(weapon_chance_panel);
    
    //Set up the UI for hero card Modifiers
    hero_chance_panel.add(new JLabel("Class Cards recieve a "));
    hero_chance_field.setPreferredSize(new Dimension(30, 25));
    hero_chance_panel.add(hero_chance_field);
    hero_chance_panel.add(new JLabel("% boost."));
    add(hero_chance_panel);
    
    //Set up the UI for legendary card Modifiers
    legendary_chance_panel.add(new JLabel("Legandary Cards recieve a "));
    legendary_chance_field.setPreferredSize(new Dimension(30, 25));
    legendary_chance_panel.add(legendary_chance_field);
    legendary_chance_panel.add(new JLabel("% boost."));
    add(legendary_chance_panel);
    
    
    if (enabled==false)
    {
      spell_chance_field.setEnabled(false);
      weapon_chance_field.setEnabled(false);
      hero_chance_field.setEnabled(false);
      legendary_chance_field.setEnabled(false);
    }
    
    
  }
  public void transferModifiers(){
    /*
    Get information on the selected modifiers so it can be given to the
    text file.
    */
      
    main_code.output_deck_text.add(spell_chance_field.getText());
    main_code.output_deck_text.add(weapon_chance_field.getText());
    main_code.output_deck_text.add(hero_chance_field.getText());
    main_code.output_deck_text.add(legendary_chance_field.getText());
  }
  
}