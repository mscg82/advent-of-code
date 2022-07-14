package com.mscg.utils.bfs;

@SuppressWarnings("java:S119")
public interface BfsVisitorBuilderStep8<NODE, NODE_ID, ADJACENT>
{

	BfsVisitor<NODE, NODE_ID, ADJACENT> build();

}
