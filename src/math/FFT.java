package math;

public class FFT {

    //Total number to run FFT, have to be the power exponent of 2
    int mTotalNum;
    int mMi; 

    // Lookup tables.  Only need to recompute when size of FFT changes.
    double[] cos;
    double[] sin;


    public FFT(int number, int winHalfLen) {
        this.mTotalNum = number;
        this.mMi = (int) (Math.log(number) / Math.log(2));

        // Make sure totalNum is a power of 2
        if (number != (1 << mMi)) {
            throw new RuntimeException("FFT length must be power of 2");
        }

        // precompute tables
        cos = new double[number / 2];
        sin = new double[number / 2];

        for (int i = 0; i < number / 2; i++) {
            cos[i] = Math.cos(-2 * Math.PI * i / number);
            sin[i] = Math.sin(-2 * Math.PI * i / number);
        }
    }

    /**
     * *************************************************************
     * fft.c Douglas L. Jones University of Illinois at Urbana-Champaign January
     * 19, 1992 http://cnx.rice.edu/content/m12016/latest/
     * <p>
     * fft: in-place radix-2 DIT DFT of a complex input
     * <p>
     * input: totalNum: length of FFT: must be a power of two m: totalNum = 2**m
     * input/output mData1: double array of length totalNum with real part of
     * data mData2: double array of length totalNum with imag part of data
     * <p>
     * Permission to copy and use this program is granted as long as this header
     * is included.
     ***************************************************************
     * @param x
     * @param y
     */
    public void fft(double[] x, double[] y) {
        int i, j, k, n1, n2, a;
        double c, s, e, t1, t2;

        // Bit-reverse
        j = 0;
        n2 = mTotalNum / 2;
        for (i = 1; i < mTotalNum - 1; i++) {
            n1 = n2;
            while (j >= n1) {
                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;

            if (i < j) {
                t1 = x[i];
                x[i] = x[j];
                x[j] = t1;
                t1 = y[i];
                y[i] = y[j];
                y[j] = t1;
            }
        }

        // FFT
        n1 = 0;
        n2 = 1;

        for (i = 0; i < mMi; i++) {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j = 0; j < n1; j++) {
                c = cos[a];
                s = sin[a];
                a += 1 << (mMi - i - 1);

                for (k = j; k < mTotalNum; k = k + n2) {
                    t1 = c * x[k + n1] - s * y[k + n1];
                    t2 = s * x[k + n1] + c * y[k + n1];
                    x[k + n1] = x[k] - t1;
                    y[k + n1] = y[k] - t2;
                    x[k] = x[k] + t1;
                    y[k] = y[k] + t2;
                }
            }
        }
    }

}
