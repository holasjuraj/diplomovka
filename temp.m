1;

% x = linspace(5, 50, 100); %1:20;
% y = linspace(5, 50, 100); %1:20;
% [xx, yy] = meshgrid(x, y);

% function res = choose2(n)
% 	res = n * (n-1) / 2;
% end

% function res = tpfp(x, y)
% 	res = (choose2(x) + choose2(y) + choose2(25)) / (x*y + 25*(x + y));
% end

% res = [];
% for a = 1:100
% 	for b = 1:100
% 		res(a, b) = tpfp(x(a), y(b));
% 	end
% end

% meshc(xx, yy, res);

for b = linspace(0, 1, 10)
b
tp = 20;
tn = 72;
fp = 20;
fn = 24;

p = tp / (tp+fp);
r = tp / (tp+fn);

rid = (tp + tn)  /  (tp + tn + fp + fn)

fb1 = ((b^2 + 1) * p * r)  /  (b^2 * p + r)

fb2 = ((b^2 + 1) * tp)  /  ((b^2 + 1)*tp + b^2*fn + fp)
end