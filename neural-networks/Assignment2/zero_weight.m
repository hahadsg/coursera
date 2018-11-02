function [model] = zero_weight()

batchsize=100;
numhid1 = 50;  % Dimensionality of embedding space; default = 50.
numhid2 = 200;  % Number of units in hidden layer; default = 200.

% LOAD DATA.
% [train_input, train_target, valid_input, valid_target, ...
%   test_input, test_target, vocab] = load_data(batchsize);
load data.mat;
numdims = size(data.trainData, 1);
D = numdims - 1;
train_input = data.trainData(1:D, :);
train_target = data.trainData(D + 1, :);
valid_input = data.validData(1:D, :);
valid_target = data.validData(D + 1, :);
test_input = data.testData(1:D, :);
test_target = data.testData(D + 1, :);
vocab = data.vocab;

[numwords, batchsize, numbatches] = size(train_input); 
vocab_size = size(vocab, 2);

disp(size(train_input));
disp(size(train_target));
disp(size(valid_input));
disp(size(valid_target));
disp(size(test_input));
disp(size(test_target));

% init weights
word_embedding_weights = zeros(vocab_size, numhid1);
embed_to_hid_weights = zeros(numwords * numhid1, numhid2);
hid_to_output_weights = zeros(numhid2, vocab_size);
hid_bias = zeros(numhid2, 1);
output_bias = zeros(vocab_size, 1);

expansion_matrix = eye(vocab_size);
% tiny = exp(-30);
tiny = 0;

% 
[embedding_layer_state, hidden_layer_state, output_layer_state] = ...
  fprop(train_input, word_embedding_weights, embed_to_hid_weights,...
        hid_to_output_weights, hid_bias, output_bias);
datasetsize = size(train_input, 2);
expanded_train_target = expansion_matrix(:, train_target);
CE = -sum(sum(...
  expanded_train_target .* log(output_layer_state + tiny))) / datasetsize;
fprintf(1, '\rFinal Train CE %.3f\n', CE);


end