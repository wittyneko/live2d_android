//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jp.live2d.motion;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import jp.live2d.ALive2DModel;
import jp.live2d.ModelContext;
import jp.live2d.motion.MotionQueueManager.MotionQueueEnt;
import jp.live2d.util.UtFile;
import jp.live2d.util.c;
import jp.live2d.util.d;

/**
 * 动作文件解析
 */
public class L2DMotion extends AMotion {
    static final String e = "VISIBLE:";
    static final String f = "LAYOUT:";
    ArrayList g = new ArrayList();
    float h;
    int i;
    int j;
    int k;
    boolean l;
    int m;
    float n;
    static int o = 0;
    static transient L2DMotion.a p = new L2DMotion.a();
    static final int q = 1;
    private jp.live2d.util.c c_c = new c();

    public L2DMotion() {
        this.m = o++;
        this.h = 30.0F;
        this.i = 0;
        this.l = false;
        this.k = -1;
        this.n = 0.0F;
    }

    public static L2DMotion loadMotion(InputStream bin) {
        byte[] var1 = UtFile.load(bin);
        L2DMotion var2 = loadMotion(var1);
        return var2;
    }

    public synchronized static L2DMotion loadMotion(byte[] str) {
        L2DMotion var1 = new L2DMotion();
        HashMap var2 = new HashMap();
        HashMap var3 = new HashMap();
        int[] var4 = new int[1];
        int var5 = str.length;
        var1.i = 0;

        for(int var6 = 0; var6 < var5; ++var6) {
            char var7 = (char)(str[var6] & 255);
            if(var7 != 10 && var7 != 13) {
                if(var7 == 35) {
                    while(var6 < var5 && str[var6] != 10 && str[var6] != 13) {
                        ++var6;
                    }
                } else {
                    int var8;
                    int var9;
                    float var12;
                    if(var7 == 36) {
                        var8 = var6;
                        var9 = -1;

                        int var15;
                        for(var15 = -1; var6 < var5; ++var6) {
                            var7 = (char)(str[var6] & 255);
                            if(var7 == 13 || var7 == 10) {
                                break;
                            }

                            if(var7 == 58) {
                                var15 = var6 + 1;
                            } else if(var7 == 61) {
                                var9 = var6;
                                break;
                            }
                        }

                        if(var9 >= 0) {
                            float var17 = -1.0F;

                            for(var6 = var9 + 1; var6 < var5; ++var6) {
                                var7 = (char)(str[var6] & 255);
                                if(var7 == 13 || var7 == 10) {
                                    break;
                                }

                                if(var7 != 44 && var7 != 32 && var7 != 9) {
                                    var12 = (float) d.a(str, var5, var6, var4);
                                    if(var4[0] > 0) {
                                        var17 = var12;
                                    }

                                    var6 = var4[0] - 1;
                                }
                            }

                            if(var9 == var8 + 4 && str[var8 + 1] == 102 && str[var8 + 2] == 112 && str[var8 + 3] == 115 && 5.0F < var17 && var17 < 121.0F) {
                                var1.h = var17;
                            }

                            if(str[var8 + 1] == 102 && str[var8 + 2] == 97 && str[var8 + 3] == 100 && str[var8 + 4] == 101 && str[var8 + 5] == 105 && str[var8 + 6] == 110) {
                                if(str[var8 + 7] == 61) {
                                    if(0.0F <= var17) {
                                        var1.a = (int)var17;
                                    }
                                } else if(str[var8 + 7] == 58) {
                                    var2.put(new String(str, var15, var9 - var15), Integer.valueOf((int)var17));
                                }
                            }

                            if(str[var8 + 1] == 102 && str[var8 + 2] == 97 && str[var8 + 3] == 100 && str[var8 + 4] == 101 && str[var8 + 5] == 111 && str[var8 + 6] == 117 && str[var8 + 7] == 116) {
                                if(str[var8 + 8] == 61) {
                                    if(0.0F <= var17) {
                                        var1.b = (int)var17;
                                    }
                                } else if(str[var8 + 8] == 58) {
                                    var3.put(new String(str, var15, var9 - var15), Integer.valueOf((int)var17));
                                }
                            }

                            var6 = var4[0] - 1;
                        }

                        while(var6 < var5 && str[var6] != 10 && str[var6] != 13) {
                            ++var6;
                        }
                    } else if(97 <= var7 && var7 <= 122 || 65 <= var7 && var7 <= 90 || var7 == 95) {
                        var8 = var6;

                        for(var9 = -1; var6 < var5; ++var6) {
                            var7 = (char)(str[var6] & 255);
                            if(var7 == 13 || var7 == 10) {
                                break;
                            }

                            if(var7 == 61) {
                                var9 = var6;
                                break;
                            }
                        }

                        if(var9 >= 0) {
                            L2DMotion.Motion var10 = new L2DMotion.Motion();
                            if(d.a(str, var8, "VISIBLE:")) {
                                var10.m = 1;
                                var10.k = new String(str, var8, var9 - var8);
                            } else if(d.a(str, var8, "LAYOUT:")) {
                                var10.k = new String(str, var8 + 7, var9 - var8 - 7);
                                if(d.a(str, var8 + 7, "ANCHOR_X")) {
                                    var10.m = 102;
                                } else if(d.a(str, var8 + 7, "ANCHOR_Y")) {
                                    var10.m = 103;
                                } else if(d.a(str, var8 + 7, "SCALE_X")) {
                                    var10.m = 104;
                                } else if(d.a(str, var8 + 7, "SCALE_Y")) {
                                    var10.m = 105;
                                } else if(d.a(str, var8 + 7, "X")) {
                                    var10.m = 100;
                                } else if(d.a(str, var8 + 7, "Y")) {
                                    var10.m = 101;
                                }
                            } else {
                                var10.m = 0;
                                var10.k = new String(str, var8, var9 - var8);
                            }

                            var1.g.add(var10);
                            int var11 = 0;
                            p.a();

                            for(var6 = var9 + 1; var6 < var5; ++var6) {
                                var7 = (char)(str[var6] & 255);
                                if(var7 == 13 || var7 == 10) {
                                    break;
                                }

                                if(var7 != 44 && var7 != 32 && var7 != 9) {
                                    var12 = (float) d.a(str, var5, var6, var4);
                                    if(var4[0] > 0) {
                                        p.a(var12);
                                        ++var11;
                                        int var13 = var4[0];
                                        if(var13 < var6) {
                                            System.out.printf("Illegal state . @L2DMotion loadMotion()\n", new Object[0]);
                                            break;
                                        }

                                        var6 = var13 - 1;
                                    }
                                }
                            }

                            var10.l = p.b();
                            if(var11 > var1.i) {
                                var1.i = var11;
                            }
                        }
                    }
                }
            }
        }

        var1.k = (int)((float)(1000 * var1.i) / var1.h);
        Iterator var14 = var2.entrySet().iterator();

        Entry var16;
        while(var14.hasNext()) {
            var16 = (Entry)var14.next();
            var1.a((String)var16.getKey(), ((Integer)var16.getValue()).intValue());
        }

        var14 = var3.entrySet().iterator();

        while(var14.hasNext()) {
            var16 = (Entry)var14.next();
            var1.b((String)var16.getKey(), ((Integer)var16.getValue()).intValue());
        }

        return var1;
    }

    public int getDurationMSec() {
        return this.l?-1:this.k;
    }

    public int getLoopDurationMSec() {
        return this.k;
    }

    public void dump() {
        for(int var1 = 0; var1 < this.g.size(); ++var1) {
            L2DMotion.Motion var2 = (L2DMotion.Motion)this.g.get(var1);
            System.out.printf("paramID[%s] [%d]. ", new Object[]{var2.k, Integer.valueOf(var2.l.length)});

            for(int var3 = 0; var3 < var2.l.length && var3 < 10; ++var3) {
                System.out.printf("%5.2f ,", new Object[]{Float.valueOf(var2.l[var3])});
            }

            System.out.printf("\n", new Object[0]);
        }

    }

    public void updateParamExe(ALive2DModel model, long timeMSec, float _weight, MotionQueueEnt motionQueueEnt) {
        long var6 = timeMSec - motionQueueEnt.d;
        float var8 = (float)var6 * this.h / 1000.0F;
        int var9 = (int)var8;
        float var10 = var8 - (float)var9;
        int var11 = (int)(this.a == 0?1.0F:c_c.a((float)(timeMSec - motionQueueEnt.e) / (float)this.a));
        int var12 = (int)(this.b != 0 && motionQueueEnt.f >= 0L?c_c.a((float)(motionQueueEnt.f - timeMSec) / (float)this.b):1.0F);

        for(int var13 = 0; var13 < this.g.size(); ++var13) {
            L2DMotion.Motion var14 = (L2DMotion.Motion)this.g.get(var13);
            int var15 = var14.l.length;
            String var16 = var14.k;
            if(var14.m == 1) {
                float var31 = var14.l[var9 >= var15?var15 - 1:var9];
                model.setParamFloat(var16, var31);
            } else if(100 > var14.m || var14.m > 105) {
                int var17 = model.getParamIndex(var16);
                ModelContext var18 = model.getModelContext();
                float var19 = var18.getParamMax(var17);
                float var20 = var18.getParamMin(var17);
                float var21 = 0.4F;
                float var22 = 0.4F * (var19 - var20);
                float var23 = var18.getParamFloat(var17);
                float var24 = var14.l[var9 >= var15?var15 - 1:var9];
                float var25 = var14.l[var9 + 1 >= var15?var15 - 1:var9 + 1];
                float var26;
                if((var24 >= var25 || var25 - var24 <= var22) && (var24 <= var25 || var24 - var25 <= var22)) {
                    var26 = var24 + (var25 - var24) * var10;
                } else {
                    var26 = var24;
                }

                float var27;
                if(var14.n < 0 && var14.o < 0) {
                    var27 = var23 + (var26 - var23) * _weight;
                } else {
                    float var28;
                    if(var14.n < 0) {
                        var28 = (float)var11;
                    } else {
                        var28 = var14.n == 0?1.0F:c_c.a((float)(timeMSec - motionQueueEnt.e) / (float)var14.n);
                    }

                    float var29;
                    if(var14.o < 0) {
                        var29 = (float)var12;
                    } else {
                        var29 = var14.o != 0 && motionQueueEnt.f >= 0L?c_c.a((float)(motionQueueEnt.f - timeMSec) / (float)var14.o):1.0F;
                    }

                    float var30 = this.c * var28 * var29;
                    var27 = var23 + (var26 - var23) * var30;
                }

                model.setParamFloat(var16, var27);
            }
        }

        if(var9 >= this.i) {
            if(this.l) {
                motionQueueEnt.d = timeMSec;
                motionQueueEnt.e = timeMSec;
            } else {
                motionQueueEnt.c = true;
            }
        }

        this.n = this.c;
    }

    public boolean isLoop() {
        return this.l;
    }

    public void setLoop(boolean loop) {
        this.l = loop;
    }

    public float getFPS() {
        return this.h;
    }

    public void setFPS(float fps) {
        this.h = fps;
    }

    void a(String var1, int var2) {
        for(int var3 = 0; var3 < this.g.size(); ++var3) {
            L2DMotion.Motion var4 = (L2DMotion.Motion)this.g.get(var3);
            if(var1.equals(var4.k)) {
                var4.n = var2;
                return;
            }
        }

    }

    void b(String var1, int var2) {
        for(int var3 = 0; var3 < this.g.size(); ++var3) {
            L2DMotion.Motion var4 = (L2DMotion.Motion)this.g.get(var3);
            if(var1.equals(var4.k)) {
                var4.o = var2;
                return;
            }
        }

    }

    float a(String var1) {
        for(int var2 = 0; var2 < this.g.size(); ++var2) {
            L2DMotion.Motion var3 = (L2DMotion.Motion)this.g.get(var2);
            if(var1.equals(var3.k)) {
                return (float)var3.n;
            }
        }

        return -1.0F;
    }

    float b(String var1) {
        for(int var2 = 0; var2 < this.g.size(); ++var2) {
            L2DMotion.Motion var3 = (L2DMotion.Motion)this.g.get(var2);
            if(var1.equals(var3.k)) {
                return (float)var3.o;
            }
        }

        return -1.0F;
    }

    static class a {
        float[] a = new float[100];
        int b = 0;

        a() {
        }

        void a() {
            this.b = 0;
        }

        void a(float var1) {
            if(this.a.length <= this.b) {
                float[] var2 = new float[this.b * 2];
                System.arraycopy(this.a, 0, var2, 0, this.b);
                this.a = var2;
            }

            this.a[this.b++] = var1;
        }

        float[] b() {
            float[] var1 = new float[this.b];
            System.arraycopy(this.a, 0, var1, 0, this.b);
            return var1;
        }
    }

    static class Motion {
        public static final int a = 0;
        public static final int b = 1;
        public static final int c = 2;
        public static final int d = 3;
        public static final int e = 100;
        public static final int f = 101;
        public static final int g = 102;
        public static final int h = 103;
        public static final int i = 104;
        public static final int j = 105;
        String k = null;
        float[] l;
        int m;
        int n = -1;
        int o = -1;

        public Motion() {
        }
    }
}
