package com.fs.starfarer.api.characters;

import com.fs.starfarer.api.characters;
import VayraUniqueBountyIntel.java;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import org.apache.log4j.Logger;
import VayraUniqueBountyManager.java;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener.FleetDespawnReason;

//this seems important for changing the rep/bonus when the target is recruited instead of killed so... Yoink
public class BountyResult {

    public BountyResultType type;
    public int payment;
    public ReputationAdjustmentResult rep;

    public BountyResult(BountyResultType type, int payment, ReputationAdjustmentResult rep) {
        this.type = type;
        this.payment = payment;
        this.rep = rep;
    }
}


CargoAPI extraBountySalvage = Global.getFactory().createCargo(true);
 extraBountySalvage.addCommodity(Commodities.HVB, 1);
 BaseSalvageSpecial.setExtraSalvage(extraBountySalvage, probe.getMemoryWithoutUpdate(), -1);


 public class hvbHireEvent extends BaseBarEventWithPerson {
    /**
     * True if this event may be selected to be offered to the player,
     * or false otherwise.
     */
    public boolean shouldShowAtMarket(MarketAPI market) {
        return super.shouldShowAtMarket(market) && !getMarket().getFactionId().equals("Independents"); // add any conditions you want
    }

    /**
     * Set up the text that appears when the player goes to the bar
     * and the option for them to start the conversation.
     */
    @Override
    public void addPromptAndOption(InteractionDialogAPI dialog) {
        // Calling super does nothing in this case, but is good practice because a subclass should
        // implement all functionality of the superclass (and usually more)
        super.addPromptAndOption(dialog);
        regen(dialog.getInteractionTarget().getMarket()); // Sets field variables and creates a random person

        // Display the text that will appear when the player first enters the bar and looks around
        dialog.getTextPanel().addPara("Sitting in a dark corner in the bar you see a hooded" + getManOrWoman() + "who seems quite out of place in the lively bar"
        );

        // Display the option that lets the player choose to investigate our bar event
        dialog.getOptionPanel().addOption("Cuatiously approach the hooded figure", this);
    }

    /**
     * Called when the player chooses this event from the list of options shown when they enter the bar.
     */
    @Override
    public void init(InteractionDialogAPI dialog) {
        super.init(dialog);
        // Choose where the player has to travel to
        hvbHireQuestCoordinator.initQuest();

        // If player starts our event, then backs out of it, `done` will be set to true.
        // If they then start the event again without leaving the bar, we should reset `done` to false.
        done = false;

        // The boolean is for whether to show only minimal person information. True == minimal
        dialog.getVisualPanel().showPersonInfo(person, true);

        // Launch into our event by triggering the "INIT" option, which will call `optionSelected()`
        this.optionSelected(null, OptionId.INIT);
    }

    /**
     * This method is called when the player has selected some option for our bar event.
     *
     * @param optionText the actual text that was displayed on the selected option
     * @param optionData the value used to uniquely identify the option
     */
    @Override
    public void optionSelected(String optionText, Object optionData) {
        if (optionData instanceof OptionId) {
            // Clear shown options before we show new ones
            dialog.getOptionPanel().clearOptions();

            // Handle all possible options the player can choose
            switch ((OptionId) optionData) {
                case INIT:
                    // The player has chosen to walk over to the crowd, so let's tell them what happens.
                    dialog.getTextPanel().addPara("You walk over sit behind the hooded" + getManOrWoman() +
                            "who slides you a data chip before getting up and leaving.");
                    dialog.getTextPanel().addPara("According to the data chip, it seems that you can learn more by traveling to " +
                            DemoQuestCoordinator.getDestinationPlanet().getName());

                    // And give them some options on what to do next
                    dialog.getOptionPanel().addOption("Take notes and decide to travel to learn more", OptionId.TAKE_NOTES);
                    dialog.getOptionPanel().addOption("Leave", OptionId.LEAVE);
                    break;
                case TAKE_NOTES:
                    // Tell our coordinator class that the player just started the quest
                    DemoQuestCoordinator.startQuest();

                    dialog.getTextPanel().addPara("You take some notes. Whatever is on this chip, you probably should keep it to yourself");
                    dialog.getOptionPanel().addOption("Leave", OptionId.LEAVE);
                    break;
                case LEAVE:
                    // They've chosen to leave, so end our interaction. This will send them back to the bar.
                    // If noContinue is false, then there will be an additional "Continue" option shown
                    // before they are returned to the bar. We don't need that.
                    noContinue = true;
                    done = true;

                    // Removes this event from the bar so it isn't offered again
                    BarEventManager.getInstance().notifyWasInteractedWith(this);
                    break;
            }
        }
    }

    enum OptionId {
        INIT,
        TAKE_NOTES,
        LEAVE
    }
}

// adds check for completed HVBs 
MemoryAPI memory = Global.getSector().getMemory();
List<String> spentBounties = (List<String>) memory.get("$vayra_uniqueBountiesSpent");



    public class BountyResult {

        public BountyResultType type;
        public int payment;
        public ReputationAdjustmentResult rep;

        public BountyResult(BountyResultType type, int payment, ReputationAdjustmentResult rep) {
            this.type = type;
            this.payment = payment;
            this.rep = rep;
        }
    }
    
public class DemoBarEventCreator extends BaseBarEventCreator {
    @Override
    public PortsideBarEvent createBarEvent() {
        return new DemoBarEvent();
    }
}

public class DemoBaseModPlugin extends BaseModPlugin {

    /**
     * Called when the player loads a saved game.
     *
     * @param newGame true if the save game was just created for the first time.
     *                Note that there are a few `onGameLoad` methods that may be a better choice than using this parameter
     */
    @Override
    public void onGameLoad(boolean newGame) {
        super.onGameLoad(newGame);

        BarEventManager barEventManager = BarEventManager.getInstance();

        // If the prerequisites for the quest have been met (optional) and the game isn't already aware of the bar event,
        // add it to the BarEventManager so that it shows up in bars
        if (DemoQuestCoordinator.shouldOfferQuest() && !barEventManager.hasEventCreator(DemoBarEventCreator.class)) {
            barEventManager.addEventCreator(new DemoBarEventCreator());
        }
    }
}

/**
 * Coordinates and tracks the state of Demo quest.
 */
class DemoQuestCoordinator {
    /**
     * The tag that is applied to the planet the player must travel to.
     */
    private static String TAG_DESTINATION_PLANET = "Demo_destination_planet";

    static SectorEntityToken getDestinationPlanet() {
        return Global.getSector().getEntityById(TAG_DESTINATION_PLANET);
    }

    static boolean shouldOfferQuest() {
        return true; // Set some conditions
    }

    /**
     * Called when player starts the bar event.
     */
    static void initQuest() {
        chooseAndTagDestinationPlanet();
    }

    /**
     * Player has accepted quest.
     */
    static void startQuest() {
        Global.getSector().getIntelManager().addIntel(new DemoIntel());
    }

    /**
     * Very dumb method that idempotently tags a random planet as the destination.
     */
    private static void chooseAndTagDestinationPlanet() {
        if (getDestinationPlanet() == null) {
            StarSystemAPI randomSystem = Global.getSector().getStarSystems()
                    .get(new Random().nextInt(Global.getSector().getStarSystems().size()));
            PlanetAPI randomPlanet = randomSystem.getPlanets()
                    .get(new Random().nextInt(randomSystem.getPlanets().size()));
            randomPlanet.addTag(TAG_DESTINATION_PLANET);
        }
    }
}