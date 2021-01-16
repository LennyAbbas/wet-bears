/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Utils;
import frc.robot.subsystems.SwerveDrivetrain;
import frc.robot.Constants.SwerveConstants;

public class GoToPositionCommand extends CommandBase {
    private SwerveDrivetrain m_drivetrain;
    private XboxController m_controller;
    private Translation2d m_currentPosition;
    private Translation2d m_targetPosition;

    /**
     * Creates a new GoToPositionCommand.
     */
    public GoToPositionCommand(SwerveDrivetrain drivetrain, Translation2d currentPosition,
            Translation2d targetPosition) {
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(drivetrain);
        m_drivetrain = drivetrain;
        m_currentPosition = currentPosition;
        m_targetPosition = targetPosition;

    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {

        m_drivetrain.move(xSpeed, ySpeed, rotSpeed);

    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        m_drivetrain.move(0, 0, 0);
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }
}
