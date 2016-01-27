function [yy] = logsig (net)
for ix=1:size(net,1)
    for iy=1:size(net,2)
        yy(ix,iy) = 1 / (1 + exp(1)^(-net(ix,iy)));
    end
end