package com.mscg.utils.bfs;

import lombok.NonNull;

import java.util.Optional;
import java.util.function.BiFunction;

@SuppressWarnings("java:S119")
public interface BfsVisitorBuilderStep7<NODE, NODE_ID, ADJACENT>
{

	BfsVisitorBuilderStep8<NODE, NODE_ID, ADJACENT> withNextNodeMapper(
			@NonNull BiFunction<? super NODE, ? super ADJACENT, Optional<? extends NODE>> nextNodeMapper);

}
