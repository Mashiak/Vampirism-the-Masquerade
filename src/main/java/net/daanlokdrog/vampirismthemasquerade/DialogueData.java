package net.daanlokdrog.vampirismthemasquerade;

import net.minecraft.network.chat.Component;
import java.util.List;

public class DialogueData {

    public static class DialoguePage {
        public final Component topText;
        public final List<DialogueOption> options;

        public DialoguePage(Component topText, List<DialogueOption> options) {
            this.topText = topText;
            this.options = options;
        }
    }

    public static class DialogueOption {
        public final Component optionText;
        public final int packetOptionId;
        public final String nextState; 
        
        public DialogueOption(Component optionText, int packetOptionId, String nextState) {
            this.optionText = optionText;
            this.packetOptionId = packetOptionId;
            this.nextState = nextState;
        }
    }

    private DialogueData() {}
}