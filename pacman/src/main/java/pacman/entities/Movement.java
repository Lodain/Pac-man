package pacman.entities;

public final class Movement {
    
    /**
     * Checks if a movement is valid based on the tile type
     * 
     * @param c The character representing the tile where the player is trying to move
     * @return 1 if movement is valid, 0 if movement is invalid, 2 the player is in a ghost, 
     * 3 the player is in a key, 4 the player is in a gate, 5 the player is in a point
     */
    public static int checkMovement(char c){
        switch(c){
            case 'W':
                return 0;
            case 'G':
                return 4;
            case 'K':
                return 3;
            case 'o':
                return 5;
            case 'C':
                return 2;
            case '.':
                return 1;
            default:
                return 0;
        }
    }

}
