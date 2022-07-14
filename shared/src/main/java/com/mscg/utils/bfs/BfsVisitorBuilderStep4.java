package com.mscg.utils.bfs;

import lombok.NonNull;

import java.util.function.Function;

@SuppressWarnings("java:S119")
public interface BfsVisitorBuilderStep4<NODE, NODE_ID, ADJACENT>
{

	BfsVisitorBuilderStep5<NODE, NODE_ID, ADJACENT> withNodeIdExtractor(
			@NonNull Function<? super NODE, ? extends NODE_ID> idExtractor);

}
