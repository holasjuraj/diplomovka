%nacitanie dat
clear
load train_set
load test_set

n_input = 1+1;
n_out = 1;

n_train = size(train_set,2);
n_test = size(test_set,2);

%vygeneruj vahy, nastav parametre
alpha = 0.1;   %  <--- uprav
mih   = 0.65;
mio   = 0.55;
n_hid = 15;     %  <--- uprav

w_hid = rand(n_hid,n_input);
w_out = rand(n_out,n_hid+1);

dw_out = zeros(n_out,n_hid+1);
dw_hid = zeros(n_hid,n_input);

errors = [];
E = 1;
%while (E > 0.05)
for ep = 1 : 500
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
close all;
load full_set;

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

min_data = min(data(2,:)');
max_data = max(data(2,:)');
E_test = E / (max_data - min_data);

E=0;
%data = (sortrows(data'))';
for j=1:size(data,2)
        
       %prechod
       x = data(:, j);
       x(end) = -1;
       % DOPLN - forward pass
       h = [logsig(w_hid * x); -1];
       y = w_out * h;
       
       plot_data(j,1) = y;
       plot_data(j,2) = data(2,j);
       
       E = E + (data(2,j)-y)^2;
end
E = sqrt(E / n_test);
E_overal = E / (max_data - min_data)   

plot(plot_data)