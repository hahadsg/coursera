function W = randInitializeWeights(L_in, L_out)
%RANDINITIALIZEWEIGHTS Randomly initialize the weights of a layer with L_in
%incoming connections and L_out outgoing connections
%   W = RANDINITIALIZEWEIGHTS(L_in, L_out) randomly initializes the weights 
%   of a layer with L_in incoming connections and L_out outgoing 
%   connections. 
%
%   Note that W should be set to a matrix of size(L_out, 1 + L_in) as
%   the first column of W handles the "bias" terms
%

% You need to return the following variables correctly 
W = zeros(L_out, 1 + L_in);

% ====================== YOUR CODE HERE ======================
% Instructions: Initialize W randomly so that we break the symmetry while
%               training the neural network.
%
% Note: The first column of W corresponds to the parameters for the bias unit
%

% Q：为什么theta初始化为0，theta值最后会变成非0？（但theta_
% A：因为计算cost时，有sigmoid函数，所以a不会为0，然后最后一层的d3不会为0，那delta2_grad就不会为0
%    不过由于sigmoid(0)=0.5，隐藏层每一个结点a都为0.5，所以会导致delta2_grad每一行都是一个值（除去第一列）
%    即从隐藏层所有结点到输出层某个结点的theta都会一致（除去theta0）
% （见nnCostFunction.m）

epsilon_init = 0.12;
W = rand(L_out, 1 + L_in) * 2 * epsilon_init - epsilon_init;

% =========================================================================

end
