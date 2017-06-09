
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


import java.awt.datatransfer.*;
import java.awt.Toolkit;

public class main_code extends JFrame
{
  public static JPanel main_panel=new JPanel();
  
  public static JPanel final_options_panel=new JPanel(new GridLayout(0,1));
  public static JButton to_main_button=new JButton("Main Menu");
  public static JButton remake_button=new JButton("Remake");
    
  public static List<String> hero_names = new ArrayList<String>();
  
  
  public static PrintWriter output_deck;
  public static ArrayList output_deck_text;
  
  
  public static final int default_spell_chance=250;
  public static final int default_weapon_chance=90;
  public static final int default_hero_chance=200;
  public static final int default_legendary_chance=25;
  
  public static final String[] default_mana_curve=new String[]{"0 1 6", "2 2 7", "3 3 6", "4 4 5", "5 5 3", "6 10 3"};
  
  
  static JPanel  deck_type_panel=new JPanel(new FlowLayout());
  
  static JButton quick_deck_button=new JButton("Make fully Random");
  static JButton custom_deck_button=new JButton("Make Using Custom Rules");
  
  
  
  
  static JPanel deck_options_panel=new JPanel(new BorderLayout());
  
  
  static PickHeoresPanel chosen_heroes=new PickHeoresPanel();
  static PickSetsPanel chosen_sets=new PickSetsPanel();
  static JPanel custom_options_panel=new JPanel(new GridLayout(0,1));
  
  
  static JButton advanced_button=new JButton("Advanced Options");
  static JButton custom_make_button=new JButton("Make Deck");
  
  static AdvancedOptionsPanel advanced_panel=new AdvancedOptionsPanel();
  
  
  static JPanel final_decks_full_panel=new JPanel(new BorderLayout());
  
  static JPanel display_decks_panel=new JPanel(new FlowLayout());
  
  
  
  
  public static void main(String args[]){
    new main_code();
  }
  
  
  
  public main_code()
  {
    
    try{
      
      
      //String pythonScriptPath = "\\Users\\Bryan\\Documents\\java\\hearthstoneDeckMaker\\pythn\\hearsthone_random_deck_generator.py";
      //String[] cmd = new String[2];
      //cmd[0] = "python";
      //cmd[1] = pythonScriptPath;
      
      //Runtime rt = Runtime.getRuntime();
      //Process pr = rt.exec(cmd);
      
      String command = "python /c start python pythn/hearsthone_random_deck_generator.py";
      Process p = Runtime.getRuntime().exec(command );
      
    }
    catch (IOException e) {
      System.out.println(e);
    }
    
		//Initialize all of the hero names so they don't need to be typed in later
    hero_names.add("Druid");
    hero_names.add("Hunter");
    hero_names.add("Mage");
    hero_names.add("Paladin");
    hero_names.add("Priest");
    hero_names.add("Rogue");
    hero_names.add("Shaman");
    hero_names.add("Warlock");
    hero_names.add("Warrior");
    
    deck_type_panel.add(quick_deck_button);
    deck_type_panel.add(custom_deck_button);
    
    main_panel.add(deck_type_panel);
    
		//Button to re-roll decks that were made
    remake_button.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
        outputDeckToFile();
        readDeckFile();
        SwingUtilities.updateComponentTreeUI(main_panel);
      }
    }
    );
    
		//Button to return to the main menu
    to_main_button.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
        main_panel.removeAll();
        
        main_panel.add(deck_type_panel);
        SwingUtilities.updateComponentTreeUI(main_panel);
      }
    }
    );
    
    final_options_panel.add(remake_button);
    
		//button to make a fully random deck
    quick_deck_button.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
        transferQuickDeck();
        outputDeckToFile();
        
        readDeckFile();
        
        main_panel.removeAll();
        final_options_panel.add(to_main_button);
        main_panel.add(final_decks_full_panel);
        
        
        SwingUtilities.updateComponentTreeUI(main_panel);
        
      }
    }
    );
    
		//Button to edit features of the random deck
    custom_deck_button.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
        main_panel.removeAll();
        
        
        custom_options_panel.add(to_main_button);
        main_panel.add(deck_options_panel);
        
        
        SwingUtilities.updateComponentTreeUI(main_panel);
      }
    }
    );
    
    
    custom_options_panel.add(advanced_button);
    custom_options_panel.add(custom_make_button);
    
    deck_options_panel.add(chosen_heroes, BorderLayout.NORTH);
    
    deck_options_panel.add(chosen_sets, BorderLayout.CENTER);
    deck_options_panel.add(custom_options_panel, BorderLayout.SOUTH);
    
    //Button to access that advanced options for making a deck
    advanced_button.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
        main_panel.removeAll();
        
        
        main_panel.add(advanced_panel);
        SwingUtilities.updateComponentTreeUI(main_panel);
      }
    }
    );
		
		//Button to sumbit custom rules for deck generation
    custom_make_button.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
        int i=chosen_heroes.all_heroes.size()-1;
        boolean picked_hero=false;
        while (i>=0 && picked_hero==false){
          if (((PickHeoresPanel.HeroSelectable)chosen_heroes.all_heroes.get(i)).check_box.isSelected()==true && ((PickHeoresPanel.HeroSelectable)chosen_heroes.all_heroes.get(i)).amount_field.getText().compareTo("0")!=0){
            picked_hero=true;
          }
          i-=1;
        }
        if (picked_hero==false){
          JOptionPane.showMessageDialog(main_panel, "Please select at least one class for your deck.");
          return;
        }
        
        i=chosen_sets.all_sets.size()-1;
        boolean picked_set=false;
        while (i>=0 && picked_set==false){
          if (((PickSetsPanel.SetCheckBox)chosen_sets.all_sets.get(i)).isSelected()==true){
            picked_set=true;
          }
          i-=1;
        }
        if (picked_set==false){
          JOptionPane.showMessageDialog(main_panel, "Please select select at least one set for your deck.");
          return;
        }
        
        
        
        
        
        transferCustomDecks();
        outputDeckToFile();
        
        readDeckFile();
        
        main_panel.removeAll();
        final_options_panel.add(to_main_button);
        main_panel.add(final_decks_full_panel);
        
        
        SwingUtilities.updateComponentTreeUI(main_panel);
        
      }
    }
    );
    
    final_decks_full_panel.add(display_decks_panel, BorderLayout.CENTER);
    final_decks_full_panel.add(final_options_panel, BorderLayout.SOUTH);
    
    
    add(main_panel);
    
    setPreferredSize(new Dimension(1000, 1000));
    pack();
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);
  }
  
  
  public void transferQuickDeck(){
    output_deck_text=new ArrayList();
    String hero_to_use=hero_names.get(new Random().nextInt(hero_names.size()));
    output_deck_text.add("random");
    
    output_deck_text.add("");
    
    for (int i=0; i<main_code.chosen_sets.all_sets.size(); i+=1){
      PickSetsPanel.SetCheckBox temp_box=(PickSetsPanel.SetCheckBox)main_code.chosen_sets.all_sets.get(i);
      if (temp_box.is_standard==true){
        output_deck_text.add(temp_box.name);
      }
    }
    
    output_deck_text.add("");
    
    output_deck_text.add("0 10 30");
    output_deck_text.add("true");
    output_deck_text.add("");
    output_deck_text.add("100");
    output_deck_text.add("100");
    output_deck_text.add("100");
    output_deck_text.add("100");
    output_deck_text.add("");
    output_deck_text.add("0");
    output_deck_text.add("");
    output_deck_text.add("false");
    output_deck_text.add("");
    output_deck_text.add("false");
    
  }
  
  
  
  public void transferCustomDecks(){
    output_deck_text=new ArrayList();
    chosen_heroes.transferHeroes();
    output_deck_text.add("");
    chosen_sets.transferSets();
    output_deck_text.add("");
    advanced_panel.transferAdvanced();
  }
  
  public void outputDeckToFile(){
    File file_to_del=new File("pythn\\deckFinishedText.txt");
    file_to_del.delete();
    
    
    try{
      output_deck=new PrintWriter("pythn\\deckInfoText.txt", "UTF-8");
      
      int start_index=0;
      
      if (((String)output_deck_text.get(start_index)).compareTo("random")==0){
        String hero_to_use=hero_names.get(new Random().nextInt(hero_names.size()));
        output_deck.println(hero_to_use+" 1");
        start_index+=1;
      }
      for (int i=start_index; i<output_deck_text.size(); i++){
        output_deck.println(output_deck_text.get(i));
      }
      output_deck.close();
    }
    catch (IOException e) {
      System.out.println(e);
    }
  }
  
  
  
  
  
  public void readDeckFile(){
    boolean found_file=false;
    List<String> fileArray=new ArrayList();
    while (found_file==false){
      try{
        display_decks_panel.removeAll();
        
        File file_to_load=new File("pythn\\deckFinishedText.txt");
        Charset text_file_charset=Charset.forName("ISO-8859-1");
        
        fileArray=Files.readAllLines(file_to_load.toPath(), text_file_charset);
        
        while (fileArray.size()==0){
          fileArray=Files.readAllLines(file_to_load.toPath(), text_file_charset);
        }
        
        file_to_load.delete();
        found_file=true;
      }
      catch (IOException e){}
    }
    
    
    //Read textfile
    int line_index=0;
    while (line_index<fileArray.size()){
      
      //Get deck hero
      JPanel temp_deck_panel=new JPanel(new BorderLayout());
      temp_deck_panel.add(new JLabel(fileArray.get(line_index), SwingConstants.CENTER), BorderLayout.NORTH);
      line_index++;
      
      String clipboard_text="";
      
      //Build lable for deck cards
      JLabel temp_cards_label=new JLabel("<html><body>", SwingConstants.CENTER);
      
      int extra_lines=0;
      while (fileArray.get(line_index).compareTo("")!=0){
        temp_cards_label.setText(temp_cards_label.getText()+fileArray.get(line_index)+"<br>");
        
        if (fileArray.get(line_index).substring(0,1).compareTo("2")==0){
          extra_lines+=1;
        }
        line_index++;
        
        clipboard_text+=fileArray.get(line_index)+"\n";
        line_index++;
      }
      
      //final String clipboard_text_final=temp_cards_label.getText().replaceAll("<br>", "\n").replaceAll("<html><body>", "");
      String clipboard_text_final=clipboard_text;
      
      JButton copy_clipboard_button=new JButton("Copy to Clipboard");
      copy_clipboard_button.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent event){
      
          StringSelection stringSelection = new StringSelection(clipboard_text_final);
          Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
          clpbrd.setContents(stringSelection, null);
        }
      }
      );
      
      while (extra_lines!=0){
        temp_cards_label.setText(temp_cards_label.getText()+"<br>");
        extra_lines-=1;
      }
      temp_cards_label.setText(temp_cards_label.getText()+"</body></html>");
      temp_deck_panel.add(temp_cards_label, BorderLayout.CENTER);
      
      
      temp_deck_panel.add(copy_clipboard_button, BorderLayout.SOUTH);
      
      
      display_decks_panel.add(temp_deck_panel);
      
      line_index++;
    }
  }
}