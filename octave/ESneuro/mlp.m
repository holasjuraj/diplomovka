%nacitanie dat
clear all;
dataset = dlmread('set2full-0.100.txt');
dataset = dataset';
dataset = [dataset(3,:); dataset(1,:)];
dataset = dataset(:,dataset(1,:) ~= dataset(2,:));

dataset = dataset(:, 1:200);

[Y, I] = sort(dataset(2, :));
dataset = dataset(:, I);
% dataset = [0:0.5:100; logsig(-100:100)*80+10];

means = mean(dataset')';
stds = std(dataset')';
dataset = (dataset .- means) ./ stds;

n_train = round(size(dataset,2) * 0.8);
train_set = dataset(:, 1:n_train);
test_set = dataset(:, n_train+1:end);
n_test = size(test_set,2);

n_input = 1+1;
n_out = 1;

%vygeneruj vahy, nastav parametre
alpha = 0.1;   %  <--- uprav
mih   = 0;%.65;
mio   = 0;%.55;
n_hid = 15;     %  <--- uprav

w_hid = rand(n_hid,n_input);
w_out = rand(n_out,n_hid+1);

dw_out = zeros(n_out,n_hid+1);
dw_hid = zeros(n_hid,n_input);

errors = [];
E = 1;
%while (E > 0.05)
for ep = 1 : 200
  ep
   % trenovanie
   train_set = train_set(:,randperm(n_train));
   for j = 1:n_train
       x = train_set(:, j);
       x(end) = -1;
       % DOPLN - forward pass
       h = [logsig(w_hid * x); -1];
       y = w_out * h;
       
       % vyratanie targetu
       target = train_set(n_input, j);
       
       %vyrataj chybu na vrstvach
       % DOPLN - backward pass + trenovanie
       sigma_out = target - y;
       w_out_unbias = w_out(:, 1:end-1);
       h_unbias = h(1:end-1);
       sigma_hid = w_out_unbias' * sigma_out .* h_unbias .* (1 - h_unbias);
              
       w_out = w_out + alpha*sigma_out*h' + mio*dw_out;
       w_hid = w_hid + alpha*sigma_hid*x' + mih*dw_hid;
       
       dw_out = alpha*sigma_out*h' + mio*dw_out;
       dw_hid = alpha*sigma_hid*x' + mih*dw_hid;
   end
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% testovanie
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% close all;

E = 0;
for j=1:n_test    
    %prechod
       x = test_set(:, j);
       x(end) = -1;
       % DOPLN - forward pass
       h = [logsig(w_hid * x); -1];
       y = w_out * h;
       
    % vyratanie targetu
    target = test_set(n_input,j);
       
    % spocitava pocet spravne urcenych vzoriek
    E = E + (target-y)^2;
end

E = sqrt(E / n_test);

min_data = min(dataset(2,:)');
max_data = max(dataset(2,:)');
E_test = E / (max_data - min_data);

E=0;
%dataset = (sortrows(dataset'))';
predicted = zeros(1, size(dataset,2));
for j=1:size(dataset,2)
        
       %prechod
       x = dataset(:, j);
       x(end) = -1;
       % DOPLN - forward pass
       h = [logsig(w_hid * x); -1];
       y = w_out * h;
       
       predicted(j) = y;
       
       E = E + (dataset(2,j)-y)^2;
end
E = sqrt(E / n_test);
E_overal = E / (max_data - min_data)   

% figure;
hold off;
plot(0);
dataset = (dataset .* stds) .+ means;
predicted = (predicted .* stds(1)) .+ means(1);
input = dataset(1,:);
target = dataset(2,:);
hold on;
plot(input, target,'.b');
plot(predicted, target,'.r');
plot(input, predicted,'.-g');
plot([0:100], [0:100],'-k');
hold off;