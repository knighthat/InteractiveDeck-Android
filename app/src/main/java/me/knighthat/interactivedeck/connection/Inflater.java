/*
 * Copyright (c) 2023. Knight Hat
 * All rights reserved.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use,copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.knighthat.interactivedeck.connection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import me.knighthat.lib.logging.Log;

public class Inflater {

    public static boolean isGzip( @NotNull JsonElement jsonElement ) {
        if ( !jsonElement.isJsonArray() )
            return false;
        JsonArray bytes = jsonElement.getAsJsonArray();
        // Check GZip magic numbers
        byte firstByte = bytes.get( 0 ).getAsByte();
        byte secondByte = bytes.get( 1 ).getAsByte();
        return firstByte == (byte) 0x1F && secondByte == (byte) 0x8B;
    }

    public static byte[] inflate( byte[] deflated ) {
        byte[] result = new byte[0];

        try ( ByteArrayInputStream bais = new ByteArrayInputStream( deflated ) ;
              GZIPInputStream gzip = new GZIPInputStream( bais ) ;
              ByteArrayOutputStream baos = new ByteArrayOutputStream() ) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ( ( bytesRead = gzip.read( buffer ) ) != -1 )
                baos.write( buffer, 0, bytesRead );

            gzip.close();
            result = baos.toByteArray();
        } catch ( IOException e ) {
            Log.err( "Error occurs while inflating data", false );
            e.printStackTrace();
        }

        Log.deb( "Inflated bytes: " + Arrays.toString( result ) );
        return result;
    }

    public static @NotNull JsonArray inflate( @NotNull JsonArray deflated ) {
        if ( !isGzip( deflated ) )
            return deflated;

        byte[] deflatedBytes = new byte[deflated.size()];
        for ( int i = 0 ; i < deflatedBytes.length ; i++ )
            deflatedBytes[i] = deflated.get( i ).getAsByte();

        String bytesToString = new String( inflate( deflatedBytes ) );
        Log.deb( "Inflated in string: " + bytesToString );

        return JsonParser.parseString( bytesToString ).getAsJsonArray();

    }
}
