import os

import random

import time

#Note: text dump was taken from: https://hearthstonejson.com/


#Global variables
#Chance to get different types of cards
legendaryChance=1/25

spellChance=1.60
weaponChance=1.25


classChance=0.4

equalHeros=["neutral", "jade lotus", "kabal", "grimy goons"]

allExpDict={}
allExpDict["Classic"]=["expert1", "core"]
allExpDict["Hall of Fame"]=["hof"]
allExpDict["Naxxramas"]=["naxx"]
allExpDict["Goblins Vs Gnomes"]=["gvg"]
allExpDict["Blackrock Mountain"]=["brm"]
allExpDict["The Grand Tournament"]=["tgt"]
allExpDict["League of Explorers"]=["loe"]
allExpDict["Whispers of the Old Gods"]=["og"]
allExpDict["One Night In Karazhan"]=["kara"]
allExpDict["Mean Streets of Gadgetzan"]=["gangs"]
allExpDict["Journey to Un'Goro"]=["ungoro"]
#"promo"?

expToUse=[]
                
enforceCurve=True

use_legendary_rules=True

deck_size=30

        
class Card(object):
    
    allCards={}
    
    def __init__(self, card_data):
        
        self.in_pools=[]
        self.card_entourage=[]
        self.enable_reno=False
        self.attribute_entourage=[]


        # set up basic information about self
        self.name=card_data['"name":']
        self.cost=card_data['"cost":']
        self.category=card_data['"type":'].lower()
        self.expansion=card_data['"set":'].lower()
        
        if card_data['"rarity":'].lower()=="free":
            self.quality="common"
        else:
            self.quality=card_data['"rarity":'].lower()
        
        
        
        
        self.card_attributes=[]

        # Add the card's mechanics to attributes
        if '"mechanics":' in card_data:
            while "," in card_data['"mechanics":']:
                self.card_attributes+=[card_data['"mechanics":'][:card_data['"mechanics":'].index(",")]]
                card_data['"mechanics":']=card_data['"mechanics":'][card_data['"mechanics":'].index(",")+1:]
            self.card_attributes+=[card_data['"mechanics":']]

        # Add the card's race to attributes
        if '"race":' in card_data:
            while "," in card_data['"race":']:
                self.card_attributes+=[card_data['"race":'][:card_data['"race":'].index(",")].lower()]
                card_data['"race":']=card_data['"race":'][card_data['"race":'].index(",")+1:]
            self.card_attributes+=[card_data['"race":'].lower()]

        # Determine this card's gang
        if '"multiClassGroup":' in card_data:
            self.hero=card_data['"multiClassGroup":'].lower().replace("_", " ")
        else:
            self.hero=card_data['"cardClass":'].lower()

        # Check if the card can help complete some quests


        # Check if card can help with druid quest
        if self.hero=="druid" or self.hero=="jade lotus" or self.hero=="neutral":
            if self.category=="minion":
                if card_data['"attack":']>=5:
                    self.card_attributes+=["druid quest"]

        # Check if card can help with hunter quest
        if self.hero=="hunter" or self.hero=="grimy goons" or self.hero=="neutral":
            if self.category=="minion" and self.cost==1:
                self.card_attributes+=["hunter quest"]

        # Check if card can help with mage quest
        if self.hero=="mage" or self.hero=="kabal" or self.hero=="neutral":
            text=card_data['"text":'].lower()
            if "spell" in text and (
                            "add" in text or "shuffle" in text or "discover" in text or "create" in text) and "whenever" not in text:
                self.card_attributes+=["mage quest"]

            elif "whenever" in text and text.find("spell") != text.rfind("spell"):
                self.card_attributes+=["mage quest"]

            elif "spare part" in text or "potion" in text or "arcane missiles" in text or "roaring torch" in text or "dream card" in text or (
                    "bananas" in text and "opponent" not in text):
                self.card_attributes+=["mage quest"]

        # Check if card can help with paladin quest
        if self.hero=="paladin" or self.hero=="grimy goons" or self.hero=="neutral":
            if self.category=="spell":
                if '"playRequirements":' in card_data:
                    text=card_data['"playRequirements":'].lower()
                    if "req_minion_target" in text and "req_enemy_target" not in text:
                        self.card_attributes+=["paladin quest"]
                    elif "req_target_to_play" in text and "req_enemy_target" not in text and "req_hero_target" not in text:
                        self.card_attributes+=["paladin quest"]

        # See if it makes progress on rogue quest
        if self.hero=="rogue" or self.hero=="jade lotus" or self.hero=="neutral":
            text=card_data['"text":'].lower()
            if "return this" in text and "your hand" in text and self.category=="minion":
                self.card_attributes+=["rogue quest"]
            elif "shuffle" in text and "this" in text and self.category=="minion":
                self.card_attributes+=["rogue quest"]
            elif "return" in text and "hand" in text and "enemy" not in text and "this" not in text:
                self.card_attributes+=["rogue quest"]
            elif ("copy" in text or "copies" in text) and "minion" in text and (
                    "add" in text or "put" in text) and "enemy" not in text and "opponent" not in text:
                self.card_attributes+=["rogue quest"]
            elif "minion" in text and "shuffle" in text and "enemy" not in text and "this" not in text:
                self.card_attributes+=["rogue quest"]
            elif "minion" in text and "swap" in text and ("deck" in text or "hand" in text):
                self.card_attributes+=["rogue quest"]
                    
            

        #adds self's category to it's attributes
        if self.category=="spell":
            self.card_attributes+=["spell"]
        if self.category=="weapon":
            self.card_attributes+=["weapon"]
        if self.hero!="neutral":
            self.card_attributes+=["hero"]
        if self.quality=="legendary":
            self.card_attributes+=["legendary"]

        self.card_attributes.sort()
        self.card_attributes = tuple(self.card_attributes)

        self.amount_owned=0
        self.amount_left=0
        
        '''
        temp_text=card_data['"text":'].lower()
        
        temp_text=temp_text.replace("<b>", "")
        temp_text=temp_text.replace("</b>", "")
        temp_text=temp_text.replace("<i>", "")
        temp_text=temp_text.replace("</i>", "")
        temp_text=temp_text.replace("\\n", " ")
        temp_text=temp_text.replace("\\", "")
        temp_text=temp_text.replace("[x]", "")
        temp_text=temp_text.replace("$", "")
        temp_text=temp_text.replace("#", "")
        '''
        
        
        self.full_info=self.name+" mana:"+str(self.cost)

        Card.allCards[self.name]=self

    def GetFullEntourage(self, entourage={}):
        global use_legendary_rules
        
        if use_legendary_rules==False:
            return [entourage]

        if self.name not in entourage:
            entourage[self.name]=1
        else:
            if entourage[self.name]==1 and self.quality!="legendary":
                entourage[self.name]=2
        
        for i in self.card_entourage:
            if (i.name not in entourage) or (i.name in entourage and entourage[i.name]==1 and i.quality!="legendary"):
                entourage=i.GetFullEntourage(entourage)
                
                
        return entourage
    



    @staticmethod
    def ResetAllCards():
        '''
        ResetAllCards() --> None
        
        Resets all Card objects in the user's collection. Called before of after
        making a deck.
        '''

        for i in Card.allCards:
            c_card=Card.allCards[i]
            c_card.amount_left=c_card.amount_owned
            c_card.in_pools=[]
            
            if c_card.amount_left not in [0,1,2]:
                for ii in range(0, 10):
                    print("ERROR")
                


        return None
        
        
    def __eq__(self, other):
        '''
        C.__eq__(y) <==> C.name==y.name
        '''
        if other==None:
            return False
        
        return self.name==other.name
    
    def __lt__(self, other):
        '''
        C.__lt__(y) <==> C.cost<y.cost or (C.cost==y.cost and C.name<y.name)
        '''
            
        return self.cost<other.cost or (self.cost==other.cost and self.name<other.name)
    
    def __gt__(self, other):
        '''
        C.__gt__(y) <==> C.cost>y.cost or (C.cost==y.cost and C.name>y.name)
        '''
        return self.cost>other.cost or (self.cost==other.cost and self.name>other.name)
        
    
    
    
        
    def __str__(self):
        '''
        C.__str__()
        
        Cards who's name may return an undesired result in the Hearthstone 
        search field may return strings with extra or omitted information
        '''
        
        output=self.name
        
        #If a search field ends in ! the Hearthstone game window may change it's
        #resolution unexpectedly. Remove ! to prevent this
        if self.name[-1]=="!":
            output=output[:-1]
            
        '''
        #Adds specifications if nessesary.
        
        if output=="Alexstrasza":
            output+=" mana:9"
        
        if output=="Arcane Missiles":
            output+=" mana:1"
        
        if output=="Archmage":
            output+=" mana:6"
        
        if output=="Armorsmith":
            output+=" mana:2"
            
        if output=="Bash":
            output+=" mana:3"
            
        if output=="Buccaneer":
            output+=" Whenever"
        
        if output=="Burgle":
            output+=" mana:3"
            
        if output=="Charge":
            output+=" give"
            
        if output=="Claw":
            output+=" mana:1"
        
        if output=="Cogmaster":
            output+=" mana:1"
        
        if output=="Crush":
            output+=" mana:7"
            
        if output=="C'Thun":
            output+=" mana:10"
        
        if output=="Deathwing":
            output+=" Battlecry"
            
        if output=="DOOM":
            output+=" mana:10"
        
        if output=="Doomguard":
            output+=" mana:5"
            
        if output=="Doomsayer":
            output+=" mana:2"
            
        if output=="Duplicate":
            output+=" mana:3"
        
        if output=="Evolve":
            output+=" mana:1"
            
        if output=="Feugen":
            output+=" attack:4"
        
        if output=="Hogger":
            output+=" mana:6"
            
        if output=="Infest":
            output+=" mana:3"
            
        if output=="Lightwarden":
            output+=" whenever"
        
        if output=="Mana Wyrm":
            output+=" mana:1"
            
        if output=="Mind Control":
            output+=" take"
            
        if output=="Polymorph":
            output+=" mana:4"
            
        if output=="Sap":
            output+=" return"
            
        if output=="Shatter":
            output+=" mana:2"
            
        if output=="Shieldbearer":
            output+=" mana:1"
            
        if output=="Silence":
            output+=" mana:0"
            
        if output=="Slam":
            output+=" deal 2"
        
        if output=="Snipe":
            output+=" Secret"
            
        if output=="Stalagg":
            output+=" attack:7"
            
        if output=="Stegodon":
            output+=" attack:2"
            
        if output=="Windfury":
            output+=" give"
        
        if output=="Wisp":
            output+=" mana:0"
        
        if output=="Wrath":
            output+=" mana:2"
        
        if output=="Volcano":
            output+=" mana:5"
        '''
        
        return output
    
    def printFull(self):
        return self.full_info
    


    @staticmethod
    def readCardDump():
        '''
        Card.readCardDump() --> None
        
        Reads the information for the local card dump and makes then info 
        objects of type Card
        '''
        
        enc='cp437'
        file_to_open=open("hearthstone_all_card_names.txt", "r", encoding=enc)
        current_card=None
        for i in file_to_open:
            line=i[1:]
            while line!="":

                #Find indexes for where the info on the current card starts and ends
                card_line=""
                braces=[0,0]
                card_end=0
                while braces[0]!=braces[1] or braces[0]==0:
                    if line[card_end]=="{":
                        braces[0]+=1
                    if line[card_end]=="}":
                        braces[1]+=1
                    
                    card_end+=1
                card_line=line[:card_end]
                line=line[card_end+1:]
                temp_index=card_line.index('"collectible":')+14

                #turn the current collection of text into an object of type Card()
                if '"collectible":true' in card_line and '"type":"HERO"' not in card_line:


                    #find and fetch relavent information for the current card
                    fetch_data=['"cardClass":', '"multiClassGroup":', '"cost":', '"name":', '"rarity":', '"set":', '"type":', '"mechanics":', '"race":', '"text":', '"attack":', '"playRequirements":']
                    output_data={}
                    for ii in fetch_data:
                        if ii in card_line:
                            temp_index=card_line.index(ii)+len(ii)
                            
                            if card_line[temp_index]=='"':
                                output_data[ii]=card_line[temp_index+1:temp_index+1+card_line[temp_index+1:].index('"')]
                            elif card_line[temp_index]=='[':
                                output_data[ii]=card_line[temp_index+2:temp_index+1+card_line[temp_index+1:].index("]")].lower().replace('"', "").replace("_", " ")
                            elif card_line[temp_index]=='{':
                                output_data[ii]=card_line[temp_index+2:temp_index+1+card_line[temp_index+1:].index("}")].lower()
                            else:
                                output_data[ii]=int(card_line[temp_index:temp_index+card_line[temp_index:].index(',')])

                    if '"text":' not in output_data:
                        output_data['"text":']=""

                    current_card=Card(output_data)
                    
                    
        Card.GetLegendRules()
        print("Done")
        Card.GetCollection()
                        
        
        #CardLocal.updateCardFile()


    @staticmethod
    def GetLegendRules():
        file_to_open=open("legend_modifiers.txt", "r")
        
        file_lines=[]
        for i in file_to_open:
            if "\n" in i:
                file_lines+=[i[:-1]]
            else:
                file_lines+=[i]
                
        #Card Name
        #specific card to add
        #renoEffect
        #attribute
        #attribute add amount
        #attribute add percent
        
        c=0
        current_card=None
        while c<len(file_lines):
            current_card=Card.allCards[file_lines[c]]
            c+=1
            
            while file_lines[c].lower()!=file_lines[c]:
                current_card.card_entourage+=[Card.allCards[file_lines[c]]]
                c+=1
            
            if c<len(file_lines) and file_lines[c]=="reno effect":
                current_card.enable_reno=True
                c+=1
                
            while c<len(file_lines) and file_lines[c]!="":
                current_card.attribute_entourage+=[{}]
                current_card.attribute_entourage[-1]["attribute"]=file_lines[c]
                c+=1
                if "+" in file_lines[c]:
                    current_card.attribute_entourage[-1]["static"]=int(file_lines[c][1:])
                    c+=1
                if "%" in file_lines[c]:
                    current_card.attribute_entourage[-1]["percent"]=int(file_lines[c][:-1])*.01
                    c+=1
                
            c+=1


    @staticmethod
    def GetCollection():
        
        try:
            file_to_open=open("HS_my_cards_info.txt", "r")
        except:
            for i in Card.allCards:
                if Card.allCards[i].quality=="legendary":
                    Card.allCards[i].amount_owned=1
                    Card.allCards[i].amount_left=1
                else:
                    Card.allCards[i].amount_owned=2
                    Card.allCards[i].amount_left=2
                    
            return
        for i in file_to_open:
            if i!="\n":
                temp_card=Card.allCards[i[:i.index("$")]]
                if i[-1]=="\n":
                    temp_card.amount_owned=int(i[-2])
                    temp_card.amount_left=int(i[-2])
                else:
                    temp_card.amount_owned=int(i[-1])
                    temp_card.amount_left=int(i[-1])
                    
                    



class Deck(object):
    
    allDecks=[]
    id_num=0
    
    def __init__(self, hero, input_curve=[-1,6,7,6,5,3,3], legendary_count=1):
        
        global spellChance, weaponChance
        
        self.hero=hero
        
        
        self.legendary_count=legendary_count
        
        self.spellChance=spellChance
        self.weaponChance=weaponChance
        
        if hero in ["druid", "mage", "priest", "warlock"]:
            self.has_weapons=False
            self.weaponChance=0
        else:
            self.has_weapons=True
            self.weaponChance=weaponChance
            
        
        self.renoEffect=False
        
        self.attribute_modifiers={}
        
        self.finalDeck=[]
        
        
        if self.hero in ["hunter", "paladin", "warrior"]:
            self.gang="grimy goons"
        elif self.hero in ["mage", "priest", "warlock"]:
            self.gang="kabal"
        else:
            #if self.hero in ["druid", "rogue", "shaman"]
            self.gang="jade lotus"
            
        self.curve={}      
        
        #curve["all"] is only used for aving CardPools
        self.curve["all"]={} 
        self.curve["all"]["total"]=0 
        self.curve["all"]["linkedCosts"]=[] 
        self.curve["all"]["pools"]={}
        
        c=len(input_curve)-1
        while c!=-1:      
            if input_curve[c]==-1:
                self.curve[c]=self.curve[c+1]
                self.curve[c]["linkedCosts"]+=[c]
            else:
                self.curve[c]={}
                self.curve[c]["total"]=input_curve[c]
                self.curve[c]["linkedCosts"]=[c]
                self.curve[c]["pools"]={}
                
            c-=1
            
        c=len(input_curve)   
        c2=c-1
        while c!=11:
            self.curve[c]=self.curve[c2]
            self.curve[c]["linkedCosts"]+=[c]
            
            c+=1
        
            
        Deck.allDecks+=[self]
        self.id_num=Deck.id_num
        Deck.id_num+=1
        
        
    def MakeDeck(self, resetCards=True):
        
        
        global legendaryChance, enforceCurve, deck_size
        
        if resetCards==True:
            Card.ResetAllCards()
            CardPool.ResetPools()
        
        for i in range(0, self.legendary_count):
            legend_pool=CardPool.MakeCardPool(self, "all", True, [], self.renoEffect)
            added=False
            while added==False:
                
                
                
                to_add=legend_pool.PickCard(self)
                
                if to_add==None:
                    raise Exception("You do not have enough legendary cards for the Min legendary values you want. Error code: 1")
                    
                if self.CheckLegendaryRules(to_add)==True:
                    self.AddCard(to_add)
                    self.ApplyLegendaryRules(to_add)
                    added=True
        
                    
        curve_keys=[0,1,2,3,4,5,6,7,8,9,10]
        while len(self.finalDeck)!=deck_size:
            
            
            if enforceCurve==True:
                random.shuffle(curve_keys)
                for i in curve_keys:
                    if self.curve[i]["total"]!=0:
                        current_key=i
                        break
                
            else:
                key_roll=random.randrange(0,deck_size)
                for i in curve_keys:
                    if min(self.curve[i]["linkedCosts"])==i:
                        key_roll-=self.curve[i]["total"]
                        if key_roll<0:
                            current_key=i
                            break
                
            
            if self.curve[current_key]["total"]!=0:
                current_pool=CardPool.MakeCardPool(self, self.curve[current_key]["linkedCosts"], False, [], self.renoEffect)
                to_add=current_pool.PickCard(self)
                if to_add==None:
                    if enforceCurve==True:
                        raise Exception("You do not have enough cards. Error code: 2")
                    if enforceCurve==False:
                        self.curve[current_key]["total"]=0
                if to_add.quality=="legendary":
                    if self.CheckLegendaryRules(to_add)==True:
                        self.AddCard(to_add)
                        self.ApplyLegendaryRules(to_add)
                if to_add.quality!="legendary":
                    self.AddCard(to_add)
                
            else:
                if enforceCurve==False:
                    curve_keys.remove(current_key)
                    
                    if curve_keys==[] and len(self.finalDeck)!=deck_size:
                        raise Exception("You do not have enough cards. Error code: 3")
                    
                    
                    
            
                    
                    
                    
    def addSpecificsCheck(self, toAdds):
        global enforceCurve, expToUse
        
        canAdd=True
        
        if type(toAdds)==dict:
            temp_list=list(toAdds.keys())
            toAdds=[]
            for i in temp_list:
                toAdds+=[Card.allCards[i]]
        
        if type(toAdds)!=list:
            toAdds=[toAdds]
        
        fullEntourage=[]
        for i in toAdds:  
            if i.quality=="legendary":
                fullEntourage+=i.GetFullEntourage()
            
                
        for i in fullEntourage:
            temp_card=Card.allCards[i]
            if temp_card not in toAdds:
                toAdds+=[temp_card]
            
        for i in toAdds:
            if i.expansion not in expToUse:
                canAdd=False
        
        if enforceCurve==True:
            for i in toAdds:    
                if self.curve[i.cost]["total"]==0:
                    canAdd=False
                self.curve[i.cost]["total"]-=1
                
            for i in toAdds:   
                self.curve[i.cost]["total"]+=1
                
        else:
            for i in toAdds:   
                if self.curve[i.cost]["total"]==0:
                    canAdd=False
                
            
            
        return canAdd
        
        



                
    
    def CheckLegendaryRules(self, legendCard):
        '''
        D.CheckLegendaryRules(Card) --> bool
        
        Determines what type of legendary card was given, and delagates the task
        of finding the card's special rules to other functions accordingly.
        '''
        
        global use_legendary_rules, deck_size
        
        if use_legendary_rules==False:
            return True
        
        canAdd=True
        
        
        #If specific cards should be added, determin if this is possible
        
        if self.addSpecificsCheck(legendCard.GetFullEntourage())==False:
            canAdd=False   
        
            
            
        #If cards with attributes should be added, determin if this is possible, and add them
        if canAdd==True:
            effectsToAdd=[]
            effectAmountsToAdd=[]
            for i in legendCard.attribute_entourage:
                if "static" in i:
                    effectsToAdd+=[i["attribute"]]
                    effectAmountsToAdd+=[i["static"]]
            
            #Calculates if the deck already meets the requirements
            for i in self.finalDeck:
                for ii in range(0, len(effectsToAdd)):
                    if effectsToAdd[ii] in i.card_attributes and effectAmountsToAdd[ii]!=0:
                        effectAmountsToAdd[ii]-=1
                        
            
            effects_needed=0
            for i in effectAmountsToAdd:
                effects_needed+=i
            
            if len(self.finalDeck)+effects_needed+1>deck_size:
                canAdd=False   
                
            elif effectsToAdd!=[]:
                if self.CheckAddAttributeCards(effectsToAdd, effectAmountsToAdd)==False:
                    canAdd=False   
                
        if canAdd==False:
            print(legendCard.name, len(self.finalDeck))
        return canAdd
    
    
    def ApplyLegendaryRules(self, legendCard):
        
        global use_legendary_rules
        
        if use_legendary_rules==False:
            return
        
        #skip_rules=self.ApplySpecialLegendRules(legendCard)   
        skip_rules=[0,1,2,3]
        
        if 0 in skip_rules:
            #Apply reno effect
            if legendCard.enable_reno==True:
                self.renoEffect=True
                self.deleteCurvePools()
                
        if 1 in skip_rules:            
            #Apply adding static attribute cards
            effectsToAdd=[]
            effectAmountsToAdd=[]
            for i in legendCard.attribute_entourage:
                if "static" in i:
                    effectsToAdd+=[i["attribute"]]
                    effectAmountsToAdd+=[i["static"]]
            self.ApplyAddAttributeCards(effectsToAdd, effectAmountsToAdd)
            
        if 2 in skip_rules:               
            #Add card modifiers
            for i in legendCard.attribute_entourage:
                if i["attribute"]=="spell":
                    self.spellChance+=i["percent"]
                elif i["attribute"]=="weapon":
                    self.weaponChance+=i["percent"]
                else:
                    self.attribute_modifiers[i["attribute"]]=i["percent"]
                    
        if 3 in skip_rules:           
            #Add specific cards
            for i in legendCard.GetFullEntourage():
                temp_card=Card.allCards[i]
                if (temp_card.quality!="legendary" and self.finalDeck.count(temp_card)!=2) or (temp_card.quality=="legendary" and self.finalDeck.count(temp_card)!=1):
                    self.AddCardsOverride([temp_card])
                    
        return
    
    def ApplySpecialLegendRules(self, legendCard):
        '''
        D.ApplySpecialLegendRules(Card) --> [int]
        
        
        '''
        
        global expToUse
        
        legendName=legendCard.name
        
        output=[0,1,2,3]
        toAddCards=[]
        
        
        
        if legendName=="The Caverns Below":
            if random.randrange(0,100)==0:
                toAddCards=[Card.allCards["C'Thun"], Card.allCards["Doomcaller"], Card.allCards["Doomcaller"], Card.allCards["Shadowstep"], legendCard]
                output=[0, 2, 3]
                
                    
            
            
        canApply=True
            
        start_index=0
        done_adding=False
        while done_adding==False:
            done_adding=True
            c=start_index
            start_index=len(toAddCards)
            while c<start_index:
                if toAddCards[c].card_entourage!=[]:
                    toAddCards+=toAddCards[c].card_entourage
                    done_adding=False
                c+=1
        
        if self.addSpecificsCheck(toAddCards)==False:
            canApply=False
            
            
            
            
            
            
        if canApply==True:
            return output
        else:
            return [0,1,2,3]
                    
            



    def deleteCurvePools(self):
        '''
        D.deleteCurvePools() --> None
        
        Removes all
        '''
        for i in self.curve:
            for ii in self.curve[i]["pools"]:
                for iii in self.curve[i]["pools"][ii].pool:
                    iii.in_pools.remove(self.curve[i]["pools"][ii])
            self.curve[i]["pools"]={}


    def AddCardsOverride(self, to_add):
        '''
        D.AddCardsOverride([Card]) --> None
        
        Takes a Card or a list of Cards.
        If the cards are currently in a deck other than deck D, remove and 
        replace them. Then add cards to deck D.
        '''
            
        
            
        if type(to_add)!=list:
            to_add=[to_add]
            
        
        for temp_card in to_add:
            print(temp_card.name)
            if temp_card.amount_left==0:
                for ii in Deck.allDecks:
                    if ii.id_num!=self.id_num:
                        if temp_card in ii.finalDeck:
                            
                            ii.removeCard(temp_card)
                            
            if temp_card.amount_left!=0:                                
                self.AddCard(temp_card)
                #gives self the card
                if temp_card.quality=="legendary":
                    self.ApplyLegendaryRules(temp_card)
        
        
        return None
                            
             

    
    def AddCard(self, to_add):
        '''
        D.AddCard(Card) --> None
        
        Adds the given card to deck D
        '''
        global enforceCurve
        
        if enforceCurve==True:
            self.curve[to_add.cost]["total"]-=1
        to_add.amount_left-=1
        self.finalDeck+=[to_add]

        #if self should only have singleton cards
        if self.renoEffect==True:
            if to_add.card_attributes in self.curve["all"]["pools"]:
                temp_pool=self.curve["all"]["pools"][to_add.card_attributes]
                if to_add in temp_pool:
                    to_add.in_pools.remove(temp_pool)
                    temp_pool.remove(to_add)
                    
                    
            if to_add.card_attributes in self.curve[to_add.cost]["pools"]:
                temp_pool=self.curve[to_add.cost]["pools"][to_add.attributes]
                if to_add in temp_pool:
                    to_add.in_pools.remove(temp_pool)
                    temp_pool.remove(to_add)
                    
        #If the user is out of copies of this card
        if to_add.amount_left==0:
            while len(to_add.in_pools)!=0:
                to_add.name
                if to_add in to_add.in_pools[0].pool:
                    to_add.in_pools[0].pool.remove(to_add)  
                    to_add.in_pools[0].attribute_counts[to_add.card_attributes]-=1
                    
                to_add.in_pools.remove(to_add.in_pools[0])
                
        return None
                
                
    def CheckAddAttributeCards(self, attributesToAdd, amounts, applyAdd=False):
        '''
        D.CheckAddAttributeCards([str], [int]) --> bool
        
        Adds cards with specific attributes to deck D. If this is possible, 
        returns True and applies changes, returns False otherwise.
        '''
        if type(attributesToAdd)==str:
            attributesToAdd=[attributesToAdd]
        if type(amounts)==str:
            amounts=[amounts]

        c=0
        
        cards_added=[]
        for attribute in attributesToAdd:
            for amount in range(0, amounts[c]):
                costKey=None
                temp_curve_keys=list(self.curve.keys())
                random.shuffle(temp_curve_keys)
                for temp_key in temp_curve_keys:
                    if costKey==None and self.curve[temp_key]["total"]!=0:
                        temp_pool=CardPool(self, temp_key, False, [attribute], self.renoEffect)
                        if len(temp_pool.pool)!=0:
                            costKey=temp_key
                if costKey==None:
                    for card in cards_added:
                        self.removeCard(card)
                        CardPool.reintroduceCard(card)
                    return False
                
                temp_pool=CardPool(self, costKey, False, [attribute], self.renoEffect)
                
                temp_card=temp_pool.PickCard(self)
                cards_added+=[temp_card]
                self.AddCard(temp_card)
            c+=1
        
        if applyAdd==False:
            for card in cards_added:
                self.removeCard(card)
                CardPool.reintroduceCard(card)
            
        return True
    
    def ApplyAddAttributeCards(self, attributesToAdd, amounts):
        self.CheckAddAttributeCards(attributesToAdd, amounts, True)


                
                
    def removeCard(self, to_remove):
        '''
        D.removeCard(Card) --> None
        
        Removes the given card from deck D
        '''
        if to_remove in self.finalDeck:
            self.finalDeck.remove(to_remove)
            self.curve[to_remove.cost]["total"]+=1

            CardPool.reintroduceCard(to_remove)
            
            to_remove.amount_left+=1
        
        return None
            
            
    def MakeSimultaneous(decks):
        '''
        MakeSimultaneous([Deck]) --> None
        
        Creates all given decks at the same time, adding one card at a time to 
        each. Cards can only be added to the given decks, across all decks, a
        number of times equivalent to the number of copies the user owns.
        
        Example: If the user owns 2 copies of a card, and submits 9 decks to be
        made; that card will be used a maximum of 2 times over the course of
        making the 9 decks.
        '''
        
        global enforceCurve, deck_size
        
        Card.ResetAllCards()
        CardPool.ResetPools()
        
        
        #Add legendaries
        for i in decks:
            for ii in range(0, i.legendary_count):
                added=False
                while added==False:
                    current_pool=CardPool.MakeCardPool(i, "all", True, [], i.renoEffect)
                    to_add=current_pool.PickCard(i)
                    if to_add==None:
                        raise Exception("You do not have enough legendary cards for the Min legendary values you want. Error code: 4")
                    
                    
                    if i.CheckLegendaryRules(to_add)==True:
                        i.ApplyLegendaryRules(to_add)
                        i.AddCard(to_add)
                        added=True
        
        #adds the rest of the cards
        curve_keys=[0,1,2,3,4,5,6,7,8,9,10]
        changed=True
        while changed==True:
            changed=False
            for i in decks:
                costKey=None
                
                if enforceCurve==True:
                    #Find what card pool to make
                    random.shuffle(curve_keys)
                    for ii in curve_keys:
                        if costKey==None and i.curve[ii]["total"]!=0:
                            costKey=i.curve[ii]["linkedCosts"]
                            
                else:
                    key_roll=random.randrange(0,deck_size)
                    for i in curve_keys:
                        key_roll-=self.curve[i]["total"]
                        if key_roll<0:
                            costKey=i
                            break

                #Builds card pool, adds card
                if costKey!=None:
                    current_pool=CardPool.MakeCardPool(i, costKey, False, [], i.renoEffect)
                        
                    changed=True
                    to_add=current_pool.PickCard(i)
                    if to_add.quality=="legendary":
                        if i.CheckLegendaryRules(to_add)==True:
                            i.AddCard(to_add)
                            i.ApplyLegendaryRules(to_add)
                    if to_add.quality!="legendary":
                        i.AddCard(to_add)
                        
                if costKey==None and len(i.finalDeck)!=deck_size:
                    raise Exception("You do not have enough cards. Error code: 5")
                        
                        

        return None
    
    
        
        
                
    def DeckCardsOutput(self):
        
        deck_for_output=[]
        for i in self.finalDeck:
            if i not in deck_for_output:
                deck_for_output+=[i]
        
        output=""
        deck_for_output.sort()
        for i in deck_for_output:
            output+="{} {}\n".format(self.finalDeck.count(i), i)
            output+="{} {}\n".format(self.finalDeck.count(i), i.printFull())
        
        return output
                    
                
    def PrintDeck(self):
        print("{} Deck:  (ID: {})\n".format(self.hero, self.id_num))
        
        print(self.DeckCardsOutput())
        
        
    
    
    def FindDeck(deck_id):
        if deck_id>=len(Deck.allDecks):
            return -1
        return Deck.allDecks[deck_id]




    def __str__(self):
        return "{} ID: {}".format(self.hero, self.id_num)


    @staticmethod
    def ResetDecks():
        Deck.allDecks = []
        Deck.id_num = 0




class CardLocal(object):

    allCards=[]


    def __init__(self, name, cost, hero, amount_owned=2):
        global equalHeros
        
        self.name=name
        self.cost=cost
        
        self.hero=hero
        if hero in equalHeros:
            self.hero="neutral"
            
        self.amount_owned=amount_owned
        
        CardLocal.allCards+=[self]

        
    def __eq__(self, other):
        return self.name==other.name

    def __gt__(self, other):
        if self.hero==other.hero:
            if self.cost==other.cost:
                return self.name>other.name
            return self.cost>other.cost
        if self.hero=="unknown":
            return True
        if other.hero=="unknown":
            return False
        if self.hero=="neutral":
            return True
        if other.hero=="neutral":
            return False
        
        return self.hero>other.hero

    def __lt__(self, other):
        return (not self==other and not self>other)
    
    def __str__(self):
        return "{}${}".format(self.name, self.amount_owned)


    @staticmethod
    def updateCardFile():
        global equalHeroes

        data_merged=[]
        not_in_dump=[]

        #get all the cards from the local dump
        temp_dump_cards=list(Card.allCards)
        file_to_open=open("HS_my_cards_info.txt", "r")
        for i in file_to_open:
            if i!="\n":
                #check if current card is in the full dump
                if i[0:i.find("$")] in temp_dump_cards:
                    fromDumpIndex=temp_dump_cards.index(i[0:i.find("$")])
                    temp_card=Card.allCards[temp_dump_cards[fromDumpIndex]]
                    if i[-1]=="\n":
                        temp_card.amount_owned=int(i[-2])
                    else:
                        temp_card.amount_owned=int(i[-1])
                    data_merged+=[CardLocal(temp_card.name, temp_card.cost, temp_card.hero, temp_card.amount_owned)]

                    temp_dump_cards.remove(temp_dump_cards[fromDumpIndex])
                else:
                    if i[-1]=="\n":
                        not_in_dump+=[i[:-1]]
                    else:
                        not_in_dump+=[i[:-2]]


        if not_in_dump!=[]:
            print("The following cards were not in the full card database:")
            for i in not_in_dump:
                print(i)
            print("")
            add_missing=input("Would you like to add these cards? (y/n)")
            while add_missing!="n" and add_missing!="y":
                print("Input not understood. please enter either: y or n")
                add_missing=input("Would you like to add these cards? (y/n)")
            if add_missing=="y":
                for i in not_in_dump:
                    data_merged+=[CardLocal(i[:i.index("$")], -1, "unknown", int(i[i.index("$")+1]))]

        if temp_dump_cards!=[]:
            print("The following cards were not in the local database:")
            for i in temp_dump_cards:
                print(i)
            print("")
            add_missing=input("Would you like to add these cards? (y/n)")
            while add_missing!="n" and add_missing!="y":
                print("Input not understood. please enter either: y or n")
                add_missing=input("Would you like to add these cards? (y/n)")
            if add_missing=="y":
                for i in temp_dump_cards:
                    temp_card=Card.allCards[i]
                    data_merged+=[CardLocal(temp_card.name, temp_card.cost, temp_card.hero, temp_card.amount_owned)]

        CardLocal.printForFile()


    @staticmethod
    def printForFile():
        CardLocal.allCards.sort()
        last_hero=CardLocal.allCards[0].hero
        for i in CardLocal.allCards:
            if last_hero==i.hero:
                print(i)
            else:
                last_hero=i.hero
                print("")
                print(i)

            
class CardPool(object):
    
    allPools=[]
    
    def __init__(self, deck, costKeys, onlyLegendary, pool_attributes, renoEffect):
        '''
        D.MakeCardPool(Deck, [int], bool, [str], bool) --> None
        
        A card pool following given paramaters. A card pool refers to the
        selection of cards this code will pull from when adding the next
        card to a deck.
        First paramater is a reference to what deck will be using this card pool
        The second is a list of the cost categories that should be used.
        Third paramater refers to if only legendary cards should be considered.
        forth paramater refers to what attributes a card must have to be 
        considered.
        Fifth paramater is is the pool should have only singltons
        '''
        
        
        global expToUse
        
        
        self.pool=[]
        
        self.hero=deck.hero
        self.gang=deck.gang
        self.costKeys=costKeys
        self.onlyLegendary=onlyLegendary
        
        self.pool_attributes=pool_attributes
        
        CardPool.allPools+=[self]
        
        self.renoEffect=renoEffect
        if renoEffect==True:
            self.originalDeck_id=deck.id_num
            self.originalDeck=deck
        else:
            self.originalDeck_id=None
        
        
        if costKeys!="all" and type(costKeys)==int:
            self.costKeys=[costKeys]
        
        self.attribute_counts={}
        
            
        #Finds which cards meet the given requirements
        for i in Card.allCards:
            card=Card.allCards[i]
            addCard=True
                     
            if costKeys=="all" or card.cost in self.costKeys:
                if card.amount_left==0:
                    addCard=False
                elif not card.expansion in expToUse:
                    addCard=False
                
                elif onlyLegendary==True and card.quality!="legendary":
                    addCard=False
                    
                elif card.hero!="neutral" and card.hero!=deck.hero and card.hero!=deck.gang:
                    addCard=False
                    
                elif renoEffect==True and card in deck.finalDeck:
                    addCard=False
                    
                    
                if addCard!=False and pool_attributes!=[]:
                    for ii in pool_attributes:
                        if ii not in card.card_attributes:
                            addCard=False
                
                if addCard==True:
                    self.pool+=[card]
                    card.in_pools+=[self]
                    
                    if card.card_attributes in self.attribute_counts:
                        self.attribute_counts[card.card_attributes]+=1
                    else:
                        self.attribute_counts[card.card_attributes]=1

                    
                            
                    
        if costKeys!="all":
            temp_key=self.costKeys[0]
        else:
            temp_key="all"    
            
        self.pool_attributes.sort()
        deck.curve[temp_key]["pools"][(onlyLegendary, tuple(self.pool_attributes))]=self

    @staticmethod
    def MakeCardPool(deck, costKeys, onlyLegendary, pool_attributes, renoEffect):
        '''
        MakeCardPool(Deck, [int], bool, [str], bool) --> None
        
        Checks to see if a card pool with the given specifics already exists, if
        yes, return it, if a pool does not exist, make one
        and return it
        '''
        
        global enforceCurve
            
        #Check if this pool is already logged in deck.curve
        if costKeys!="all":
            temp_key=costKeys[0]
        else:
            temp_key="all"
        if deck.curve[temp_key]["pools"]!=None:
            temp_pools=deck.curve[temp_key]["pools"]
            pool_attributes.sort()
            if (onlyLegendary, tuple(pool_attributes)) in temp_pools:
                return temp_pools[(onlyLegendary, tuple(pool_attributes))]
            
        #Loop through all previously made pools
        for i in CardPool.allPools:
            if deck.hero==i.hero and costKeys==i.costKeys and onlyLegendary==i.onlyLegendary and (deck.renoEffect==False or i.originalDeck_id==deck.id_num):
                if len(pool_attributes)==len(i.pool_attributes):
                    found_match=True
                    for ii in pool_attributes:
                        if ii not in i.pool_attributes:
                            found_match=False
                    if found_match==True:
                        return i

        new_pool=CardPool(deck, costKeys, onlyLegendary, pool_attributes, renoEffect)

        return new_pool


    def PickCard(self, deck):
        '''
        CP.PickCard() --> Card
        
        Selects a card this CardPool and returns it
        '''
        

        global legendaryChance, classChance
        
        if len(self.pool)==0:
            print("Deck: {}\nHero: {}\nError. Not enough cards in chosen set(s).".format(deck.id_num, deck.hero))
            if deck.renoEffect==True:
                print("The Reno effect is currently enabled on this deck. would you like to disable it?")
                temp_input=input("Please enter y/n:")
                if temp_input!="y" and temp_input!="n":
                    temp_input=input("Input not understood. Please enter y/n:")
                if temp_input=="y":
                    deck.renoEffect=False
                    deck.MakeCardPool()
                if temp_input=="n":
                    raise Exception("Trying to add Card from an empty card pool in deck {}.".format(deck.hero))


        #find out what the total of the occurence weights will be for picking
        #a card from the pool
        roll_total=0
        for i in self.attribute_counts:
            roll_modifier=self.attribute_counts[i]
            if "spell" in i:
                roll_modifier=roll_modifier*deck.spellChance
            if "weapon" in i:
                roll_modifier=roll_modifier*deck.weaponChance
            if "hero" in i:
                roll_modifier=roll_modifier*classChance
            if "legendary" in i:
                if self.onlyLegendary!=True:
                    roll_modifier=roll_modifier*legendaryChance
                

            for ii in deck.attribute_modifiers:
                if ii in i:
                    roll_modifier=roll_modifier*deck.attribute_modifiers[ii]
                    
            roll_total+=roll_modifier
                    
    
        #Loops through the pool, applying the weights until it finds the card
        #it picked
        pickedCard=None
        card_roll=random.random()*roll_total
        for i in self.pool:
            
            roll_reduction=1
            
            if i.category=="spell":
                roll_reduction=roll_reduction*deck.spellChance 
            if i.category=="weapon":
                roll_reduction=roll_reduction*deck.weaponChance
            if i.hero!="neutral":
                roll_reduction=roll_reduction*classChance
            if i.quality=="legendary":
                if self.onlyLegendary!=True:
                    roll_reduction=roll_reduction*legendaryChance
            for ii in deck.attribute_modifiers:
                if ii in i.card_attributes:
                    if ii not in ["spell", "weapon", "hero", "legendary"]:
                        roll_reduction=roll_reduction*deck.attribute_modifiers[ii]
                
                
            card_roll-=roll_reduction
            
            pickedCard=i
            if card_roll<=0:
                break
        
        return pickedCard

    def removeCardFromPool(self, to_remove):
        if to_remove not in self.pool:
            return

        self.pool.remove(to_remove)
        to_remove.in_pools.remove(self)

    def addCardToPool(self, to_add):
        if to_remove in self.pool:
            return
        self.pool+=[to_add]
        to_add.in_pools+=[self]
               
        if to_add.card_attributes in self.attribute_counts:
            self.attribute_counts[to_add.card_attributes]+=1
        else:
            self.attribute_counts[to_add.card_attributes]=1
            

    @staticmethod
    def reintroduceCard(card):
        for i in CardPool.allPools:
            add_to_pool=True

            if i.costKeys!="all" and card.cost not in i.costKeys:
                add_to_pool=False

            if card.quality!="legendary" and i.onlyLegendary==True:
                add_to_pool=False

            if card.hero!="neutral" and card.hero!=i.hero and card.hero!=i.gang:
                add_to_pool=False

            for ii in i.pool_attributes:
                if ii not in card.card_attributes:
                    add_to_pool=False

            if i.renoEffect==True:
                if card in i.originalDeck.finalDeck:
                    add_to_pool=False

            if add_to_pool==True:
                i.pool+=[card]
                card.in_pools+=[i]
                
                if card.card_attributes in i.attribute_counts:
                    i.attribute_counts[card.card_attributes]+=1
                else:
                    i.attribute_counts[card.card_attributes]=1


    @staticmethod
    def ResetPools():
        CardPool.allPools=[]
        
                            
def resetAll():
    '''
    resetAll() --> None
    
    Resets decks, cards, and card pools to their state prior to building any decks.
    '''

    Deck.ResetDecks()
    Card.ResetAllCards()
    CardPool.ResetPools()


def TransferDecks(decks):
    '''
    TransferDecks([Deck]) --> None
    
    Takes given decks and saves them in a text file for the Java part to read
    '''
        
    file_to_write=open("deckFinishedText.txt", "w")
    
    for i in decks:
        deckText=i.DeckCardsOutput()
        file_to_write.write(i.hero+"\n")
        for ii in deckText:
            file_to_write.write(ii)
            
            
        file_to_write.write("\n")     
    
    
    file_to_write.close()
    
        
        
    
Card.readCardDump()


print("Start")


        

while True:
    try:
        file_to_open=open("deckInfoText.txt", "r")
        file_lines=[]
        while file_lines==[]:
            for i in file_to_open:
                file_lines+=[i]
        
                        
    except:
        file_to_open=None
    
    if file_to_open!=None:
        
        file_to_open.close()
        os.remove("deckInfoText.txt")
        
        temp_heroes=[]
        temp_expansions=[]
        temp_costs=[]    
        min_legendary=0
        make_simul=False
        expToUse=[]
        line_index=0
        
        #Get the heroes
        while file_lines[line_index]!="\n":
            for ii in range(0,int(file_lines[line_index][file_lines[line_index].index(" ")+1:-1])):
                temp_heroes+=[file_lines[line_index][0:file_lines[line_index].index(" ")].lower()]
            
            line_index+=1
            
        line_index+=1
        #Get the sets           
        while file_lines[line_index]!="\n":
            try:
                expToUse+=allExpDict[file_lines[line_index][:-1]]
            except:
                raise Exception(file_lines[line_index]+" Is not a valid expansion name.")
            line_index+=1
            
        line_index+=1
        #Get the cost info
        deck_size=0
        while file_lines[line_index]!="true\n" and file_lines[line_index]!="false\n":
            min_cost=int(file_lines[line_index][:file_lines[line_index].index(" ")])
            file_lines[line_index]=file_lines[line_index][file_lines[line_index].index(" ")+1:]
            max_cost=int(file_lines[line_index][:file_lines[line_index].index(" ")])
            file_lines[line_index]=file_lines[line_index][file_lines[line_index].index(" ")+1:]
            if max_cost!=10:
                while min_cost!=max_cost:
                    temp_costs+=[-1]
                    min_cost+=1
            deck_size+=int(file_lines[line_index][:-1])
            temp_costs+=[int(file_lines[line_index][:-1])]
            
            line_index+=1
            
        if file_lines[line_index]=="true\n":
            enforceCurve=True
        else:
            enforceCurve=False
            
        line_index+=1
        
            
        line_index+=1
        #Get the modifiers
        spellChance=int(file_lines[line_index][:-1])*.01
        line_index+=1
        weaponChance=int(file_lines[line_index][:-1])*.01
        line_index+=1
        classChance=int(file_lines[line_index][:-1])*.01
        line_index+=1
        legendaryChance=int(file_lines[line_index][:-1])*.01
        line_index+=1
        
        
        
        
        line_index+=1
        #Get the minimum legendaries
        temp_min_legendary=int(file_lines[line_index][:-1])
        
        line_index+=2            
        #Get if legend rules should be applied      
        if file_lines[line_index][:-1]=="true":
            use_legendary_rules=True
        else:
            use_legendary_rules=False
            
        line_index+=2  
        #Get if make simultanious should be applied            
        if file_lines[line_index][:-1]=="true":
            make_simul=True
        else:
            make_simul=False
                        
        print("File Read")
        
        
        
        final_decks=[]
        for hero in temp_heroes:
            final_decks+=[Deck(hero, temp_costs, temp_min_legendary)]
            if make_simul==False:
                print("Start "+hero+" Deck")
                final_decks[-1].MakeDeck()
                print("Done "+hero+" Deck\n\n")
        if make_simul==True:
            Deck.MakeSimultaneous(final_decks)
            
        for i in final_decks:
            i.PrintDeck()
            
        resetAll()
        
        TransferDecks(final_decks)
        
        print("Done Reading")
                