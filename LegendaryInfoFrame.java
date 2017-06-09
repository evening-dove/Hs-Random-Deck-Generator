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

public class LegendaryInfoFrame extends JFrame
{
  
  private LegendaryInfoFrame this_frame=this;
	
  ArrayList<LegendRuleRow> all_LegendRows=new ArrayList<LegendRuleRow>();
  
  JPanel main_panel=new JPanel(new BoarderLayout());

  JPanel header_panel=new JPanel(new BoarderLayout());

  JPanel header_view_panel=new JPanel(new FlowLayout());
  JPanel header_edit_panel=new JPanel(new FlowLayout());
  JPanel header_info_panel=new JPanel(new GridLayout(0, 5));

  JButton view_edit_button=new JButton("Edit");
  JButton view_legend_button=new JButton("Legend");

  JButton edit_add_button=new JButton("Add");
  JButton edit_delete_button=new JButton("Delete");
  JButton edit_undo_button=new JButton("Undo Changes");
  JButton edit_save_button=new JButton("Save Changes");
  JButton edit_done_button=new JButton("Done");


  JScrollPane legend_rules_SPane;
  JPanel legend_rules_panel=new JPanel(new GridLayout(1, 0));
  
  public LegendaryInfoFrame()
  {
    super();

  //Makes it so the rules can be edited
  view_edit_button.addActionListener(new ActionListener(){
    public void actionPerformed(ActionEvent event){
      for (int legendRowIndex=0;  legendRowIndex<all_LegendRows.size(); legendRowIndex+=1){
        all_LegendRows.get(legendRowIndex).changeForEdit();
      }
    }
  }
  );

  header_view_panel.add(view_edit_button);
  header_view_panel.add(view_legend_button);



  edit_delete_button.SetEnabled(false);
		
		
		
  edit_done_button.addActionListener(new ActionListener(){
    public void actionPerformed(ActionEvent event){
      for (int legendRowIndex=0;  legendRowIndex<all_LegendRows.size(); legendRowIndex+=1){
        all_LegendRows.get(legendRowIndex).changeForView();
      }
    }
  }
  );

  header_edit_panel.add(edit_add_button);
  header_edit_panel.add(edit_delete_button);
  header_edit_panel.add(edit_undo_button);
  header_edit_panel.add(edit_save_button);
  header_edit_panel.add(edit_done_button);


  header_info_panel.add(new JLabel("Name"));
  header_info_panel.add(new JLabel("Entourage"));
  header_info_panel.add(new JLabel("<html>Effect<br>Entourage</html>"));
  header_info_panel.add(new JLabel("<html>Effect<br>Modifiers</html>"));
  header_info_panel.add(new JLabel("<html>Make<br>Singleton</html>"));


  legend_rules_SPane=new JScrollPane(legend_rules_panel);
  legend_rules_SPane.createHorizontalScrollBar();

  main_panel.add(header_panel, BorderLayout.NORTH);
  main_panel.add(legend_rules_SPane, BorderLayout.CENTER);
		
		
  add(main_panel);
		
		
		
		
		
  ReadModifiersText();


  }
	
	
  public void ReadModifiersText()
  {
		
    List<String> fileArray=new ArrayList();
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
    
    //Card Name
    //specific card to add
    //renoEffect
    //attribute
    //attribute add amount
    //attribute add percent
    
    //Read textfile
    int line_index=0;
    while (line_index<fileArray.size()){
			
    String card_name;
    Map<String, int> entourage_to_add=new Map<String, int>();
    boolean reno_effect=false;
    ArrayList<String> attribue_names=new ArrayList<String>();
    ArrayList<String> attribue_add_amount=new ArrayList<String>();
    ArrayList<String> attribue_add_percent=new ArrayList<String>();
			
    card_name=fileArray.get(line_index);
		ine_index+=1;
    while (fileArray.get(line_index).toLowerCase()!=fileArray.get(line_index))
		{
      if (entourage_to_add.containsKey(fileArray.get(line_index))==false)
			{
				entourage_to_add.put(fileArray.get(line_index), 1);
			}
      if (entourage_to_add.containsKey(fileArray.get(line_index))==true)
			{
				entourage_to_add.put(fileArray.get(line_index), 2);
			}
			line_index+=1;
			}
			if (fileArray.get(line_index).conatins("reno effect"))
			{
				reno_effect=true;
				line_index+=1;
			}
			while (fileArray.get(line_index).compareTo("")!=0)
			{
				attribue_names.add(fileArray.get(line_index));
				line_index+=1;
				if (fileArray.get(line_index).conatins("+"))
				{
					attribue_add_amount.add(Integer.parseInt(fileArray.get(line_index).substring(1)));
					line_index+=1;
				}
				else{
					attribue_add_amount.add(0);
				}
				if (fileArray.get(line_index).conatins("%"))
				{
					attribue_add_percent.add(Integer.parseInt(fileArray.get(line_index).substring(0, fileArray.get(line_index).length()-2)));
					line_index+=1;
				}
				else{
					//If the attribute has no related modifiers, just set the modifier to 100
					attribue_add_percent.add(100);
				}
				
			  line_index+=1;
			}
			
			
			LegendRuleRow newRow = new LegendRuleRow(card_name);
			for (Map.Entry<String, int> card : entourage_to_add.entrySet())
			{
				newRow.addEntourage(card.getKey(), card.getValue());
			}
			newRow.setRenoEffect(reno_effect);
			for(int i=0; i<attribue_names.length(); i++)
			{
				newRow.addEffectAmount(attribue_names.get(i), attribue_add_amount.get(i));
				newRow.addEffectAmount(attribue_names.get(i), attribue_add_percent.get(i));
			}
		}
	}
	
	
	

  private class LegendRuleRow extends JPanel
  {
		private LegendRuleRow this_panel=this;



		JPanel rules_panel=new JPanel(new GridLayout(0, 5));

		JButton add_entourage_button=new JButton("+");
		JButton add_effect_amount_button=new JButton("+");
		JButton add_effect_modifier_button=new JButton("+");


		JLabel card_name;

		JPanel entourage_panel=new JPanel(new GridLayout(2, 0));
		JPanel effect_amount_panel=new JPanel(new GridLayout(2, 0));
		JPanel effect_modifier_panel=new JPanel(new GridLayout(2, 0));

		JLabel reno_effect;

		private LegendRuleRow(String card_name)
		{
			super(new BorderLayout());
			
			add(rules_panel, BorderLayout.CENTER);
			
			
			this.card_name=card_name;
			
			
			
			
			rules_panel.add(card_name);
			rules_panel.add(entourage_panel);
			rules_panel.add(effect_amount_panel);
			rules_panel.add(effect_modifier_panel);
			rules_panel.add(activate_reno);
			
			
			
			all_LegendRows.add(this);

		}


		public void addEntourage(String new_card, int add_amount){
			if (add_amount==0){
				//Adding zero of a cards to an entourage dosnt do anything, so simply remove the rule
				removeCardRule(new_card, entourage_panel);
				return;
			}
			addCardRule(new_card, add_amount, entourage_panel);
		}

		public void removeEntourage(String to_remove){
			removeAddRule(to_remove, entourage_panel);
		}


		public void addEffectAmount(String new_effect, int add_amount){
			if (add_amount==0){
				//Adding zero of the effect dosnt do anything, so simply remove the rule
				removeCardRule(new_effect, effect_amount_panel);
				return;
			}
			addCardRule(new_effect, add_amount, effect_amount_panel);
		}

		public void removeEntourage(String to_remove){
			removeAddRule(to_remove, entourage_panel);
		}


		public void addEffectModifier(String new_modifier, int add_amount){
			if (add_amount==100){
				//A modifier of 100 dosnt do anything, so simply remove the rule
				removeCardRule(new_modifier, effect_modifier_panel);
				return;
			}
			addCardRule(new_modifier, add_amount, effect_modifier_panel);
		}

		public void removeEntourage(String to_remove){
			removeAddRule(to_remove, entourage_panel);
		}



		private void addCardRule(String new_addition, int add_amount, JPnael add_to)
		{
			bool set_next=null;
			//Check to see if the rule already exists. if so, simply update it
			for (Component c : add_to.getComponents()){
				if (c.GetText()==new_addition){
					set_next=true;
				}else if (set_next==true){
					c.SetText(add_amount);
					set_next=false;
				}
			}
			
			//Create the new rule
			if (set_next==null){
				add_to.add(new JLabel(new_addition));
				add_to.add(new JTextField(add_amount));
			}
		}



		private void removeCardRule(String to_remove, JPanel remove_from)
		{
			JPanel[] remove_locations=new JPanel[2];
			bool remove_next=null;
			
			//search for the rule
			for (Component c : remove_from.getComponents()){
				if (c.GetText()==to_remove){
					//if the rule was found, save its position and signal to also
					//save the next component
					remove_locations[0]=c;
					remove_next=true;
				}else if (remove_next==true){
					//if the previous component was the rule to remove, remove this aswell.
					remove_locations[1]=c;
					remove_next=false;
				}
			}
			if (remove_next==false){
				//if the rules were found, remove them
				remove_from.remove(remove_locations[0], remove_locations[1]);
			}
		}


		public void setRenoEffect(bool reno_effect)
		{
			//Set if a card should make your deck only have singletons
			if (reno_effect){
				this.reno_effect.SetText("Yes");
			}else{
				this.reno_effect.SetText("No");
			}
		}



		protected void changeForEdit(){
			//Make the rule feilds editable
			
			changeRulePanelForEdit(entourage_panel);
			changeRulePanelForEdit(effect_amount_panel);
			changeRulePanelForEdit(effect_modifier_panel);
		}
		
		private void changeRulePanelForEdit(JPanel to_change){
			
			//Make the given rule panel ediable
			bool isName=true;
			for (Component c : to_change.getComponents()){
				if (isName==false){
					c.SetEnabled(true);
					isName=true;
				}else{
					//if the current component is the name refering to a rule
					isName=false;
				}
			}
		}

		protected void changeForView(){
			//Make the rule feilds no longer editable
			
			changeRulePanelForView(entourage_panel);
			changeRulePanelForView(effect_amount_panel);
			changeRulePanelForView(effect_modifier_panel);
		}
		
		private void changeRulePanelForView(JPanel to_change){
			//Make the given rule panel no longer ediable
			
			bool isName=true;
			for (Component c : to_change.getComponents()){
				if (isName==false){
					c.SetEnabled(false);
					isName=true;
				}else{
					//if the current component is the name refering to a rule
					isName=false;
				}
			}
		}
	}
}