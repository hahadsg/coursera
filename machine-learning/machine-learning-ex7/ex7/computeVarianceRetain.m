function var_retain_percent = computeVarianceRetain(S, K)

var_retain_percent = sum(sum(S(1:K, 1:K))) / sum(sum(S));

end;