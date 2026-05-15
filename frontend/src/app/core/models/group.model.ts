import { UserResponse } from './user.model';

export interface GroupResponse {
  id: number;
  name: string;
  description: string;
  users?: UserResponse[];
  userCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface GroupRequest {
  name: string;
  description: string;
}

export interface GroupAssignUsersRequest {
  userIds: number[];
}
