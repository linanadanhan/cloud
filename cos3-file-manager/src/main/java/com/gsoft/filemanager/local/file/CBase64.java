package com.gsoft.filemanager.local.file;

public class CBase64 {

    private static char[] S_BASE64 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    private static char S_BASE64PAD = '=';

    public final byte[] decode(String ibuf, int size) {
        int i;
        int j;
        int k;

        byte[] tmp = new byte[3];
        byte[] src = new byte[4];
		String buf1 = strAnalysis(ibuf);
		System.out.print(buf1);
        byte[] buf = buf1.getBytes();

        int b64size = (size + 2) / 3 * 4;

        byte[] m_bin = new byte[size+3];

        for (i = j = 0; i < b64size; i += 4, j += 3) {

            tmp[0] = tmp[1] = tmp[2] = 0;

            for (k = 0; k < 4; k++) {
                src[k] = base64toBYTE(buf[i+k]);
            }

            tmp[0] = (byte) ((src[0] << 2) & 0xFC);
            tmp[0] |= (src[1] >>> 4) & 0x03;
            tmp[1] = (byte) ((src[1] << 4) & 0xF0);
            tmp[1] |= (src[2] >>> 2) & 0x0F;
            tmp[2] = (byte) ((src[2] << 6) & 0xC0);
            tmp[2] |= src[3] & 0x3F;
            for (k = 0; k < 3; k++) {
                m_bin[j + k] = tmp[k];
            }
        }

        return m_bin;
    }

    public byte base64toBYTE(byte char64) {
        int i;
        for (i = 0; i < 64; i++) {
            if (S_BASE64[i] == char64) {
                return (byte) i;
            }
        }
        return 0;
    }

	public String strAnalysis(String str){
	    StringBuffer b = new StringBuffer();
        for(int o=0;o<str.length();o++){
		    char a = str.charAt(o);
			if(a=='9'){
		        char c = '1';
			    if(str.charAt(o+1)=='0'){
				    c = '9';
					
					
				}
				if(str.charAt(o+1)=='1'){
				    c = '+';
					
				}
				if(str.charAt(o+1)=='2'){
				     c = '/';
					 
				}
				if(c!='1'){
				   a = c;
				   o++;
				}
				
			}
			b.append(a);
		}
		return b.toString();
	
	}

}