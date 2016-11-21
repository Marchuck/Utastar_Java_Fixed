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

package uta.dataModel;

/**
 * Stores single value of a data.
 */
public class DataParam {
    public enum OptDirection {MIN, MAX};
    
    public DataParam() {
        mval = 0;
        moptDir = OptDirection.MAX;
    }
    
    public DataParam(String name, double val, OptDirection optdir) {
        mname = name;
        mval = val;
        moptDir = optdir;
    }
    
    public DataParam(double val, OptDirection optDir) {
        mval = val;
        moptDir = optDir;
    }
    
    public double val() {
        return mval;
    }
    
    public void setVal(double val) {
        mval = val;
    }
    
    public String name() {
        return mname;
    }
    
    public void setName(String name) {
        mname = name;
    }
    
    public OptDirection optDir() {
        return moptDir;
    }
    
    public void setOptDir(OptDirection optDir) {
        moptDir = optDir;
    }
    
    @Override
    public String toString() {
        return "('" + mname + "':" + Double.toString(mval) + ", " + moptDir + ")";
    }
    
    private double mval;
    private OptDirection moptDir;
    private String mname;
}
