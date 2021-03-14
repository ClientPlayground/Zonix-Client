package net.minecraft.client.renderer.culling;

public class ClippingHelper
{
    public float[][] frustum = new float[16][16];
    public float[] projectionMatrix = new float[16];
    public float[] modelviewMatrix = new float[16];
    public float[] clippingMatrix = new float[16];
    private static final String __OBFID = "CL_00000977";

    /**
     * Returns true if the box is inside all 6 clipping planes, otherwise returns false.
     */
    public boolean isBoxInFrustum(double p_78553_1_, double p_78553_3_, double p_78553_5_, double p_78553_7_, double p_78553_9_, double p_78553_11_)
    {
        for (int var13 = 0; var13 < 6; ++var13)
        {
            if ((double)this.frustum[var13][0] * p_78553_1_ + (double)this.frustum[var13][1] * p_78553_3_ + (double)this.frustum[var13][2] * p_78553_5_ + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * p_78553_7_ + (double)this.frustum[var13][1] * p_78553_3_ + (double)this.frustum[var13][2] * p_78553_5_ + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * p_78553_1_ + (double)this.frustum[var13][1] * p_78553_9_ + (double)this.frustum[var13][2] * p_78553_5_ + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * p_78553_7_ + (double)this.frustum[var13][1] * p_78553_9_ + (double)this.frustum[var13][2] * p_78553_5_ + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * p_78553_1_ + (double)this.frustum[var13][1] * p_78553_3_ + (double)this.frustum[var13][2] * p_78553_11_ + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * p_78553_7_ + (double)this.frustum[var13][1] * p_78553_3_ + (double)this.frustum[var13][2] * p_78553_11_ + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * p_78553_1_ + (double)this.frustum[var13][1] * p_78553_9_ + (double)this.frustum[var13][2] * p_78553_11_ + (double)this.frustum[var13][3] <= 0.0D && (double)this.frustum[var13][0] * p_78553_7_ + (double)this.frustum[var13][1] * p_78553_9_ + (double)this.frustum[var13][2] * p_78553_11_ + (double)this.frustum[var13][3] <= 0.0D)
            {
                return false;
            }
        }

        return true;
    }

    public boolean isBoxInFrustumFully(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        for (int i = 0; i < 6; ++i)
        {
            float minXf = (float)minX;
            float minYf = (float)minY;
            float minZf = (float)minZ;
            float maxXf = (float)maxX;
            float maxYf = (float)maxY;
            float maxZf = (float)maxZ;

            if (i < 4)
            {
                if (this.frustum[i][0] * minXf + this.frustum[i][1] * minYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * maxXf + this.frustum[i][1] * minYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * minXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * maxXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * minXf + this.frustum[i][1] * minYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * maxXf + this.frustum[i][1] * minYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * minXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F || this.frustum[i][0] * maxXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F)
                {
                    return false;
                }
            }
            else if (this.frustum[i][0] * minXf + this.frustum[i][1] * minYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * maxXf + this.frustum[i][1] * minYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * minXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * maxXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * minZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * minXf + this.frustum[i][1] * minYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * maxXf + this.frustum[i][1] * minYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * minXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F && this.frustum[i][0] * maxXf + this.frustum[i][1] * maxYf + this.frustum[i][2] * maxZf + this.frustum[i][3] <= 0.0F)
            {
                return false;
            }
        }

        return true;
    }
}
