package pacman.entities;

/**
 * Class for handling movement logic
 * This class provides methods to check if movements are valid based on tile types
 */
public final class Movement {
    
    /**
     * Checks if a movement is valid based on the tile type.
     * 
     * @param c The character representing the tile where the player is trying to move
     * @return Movement status code:
     *         0 - Invalid movement (wall)
     *         1 - Valid movement (empty space)
     *         2 - Ghost collision
     *         3 - Key pickup
     *         4 - Gate interaction
     *         5 - Point pickup
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
