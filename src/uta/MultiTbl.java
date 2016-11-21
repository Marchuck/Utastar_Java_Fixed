/*
Copyright (C) 2013 mc_utastar Development Team

This file is part of mc_utastar.

mc_utastar is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

mc_utastar is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with RoomEdit. If not, see <http://www.gnu.org/licenses/>.
*/
package uta;

public class MultiTbl {

    public MultiTbl(double[][][] limits, double[][] crit, int[] pref) {
        mcritNumb = crit.length;
        maltNumb = crit[0].length;
        mcritWeight = new double[mcritNumb];
        mcritScale = new boolean[mcritNumb];
        mlimits = new double[mcritNumb][][];
        for (int i = 0; i < mcritNumb; i++) {
            mlimits[i] = new double[limits[i].length][2];
        }
        mgradeTable = new double[mcritNumb][maltNumb];
        maltPreference = new int[maltNumb];
        for (int i = 0; i < mcritNumb; i++) {
            mcritWeight[i] = 1 / mcritNumb;
            mcritScale[i] = false;
            for (int k = 0; k < mlimits[i].length; k++) {
                mlimits[i][k][0] = limits[i][k][0];
                mlimits[i][k][1] = limits[i][k][1];
            }
            System.arraycopy(crit[i], 0, mgradeTable[i], 0, maltNumb);
        }
        System.arraycopy(pref, 0, maltPreference, 0, maltNumb);
    }
    
    public double[][][] mlimits;
    public boolean[] mcritScale;
    public int mcritNumb;
    public int maltNumb;
    public double[][] mgradeTable;
    public double[] mcritWeight;
    public int[] maltPreference;
}
